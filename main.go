package main

import (
	"context"
	"database/sql"
	"encoding/json"
	"log"
	"log/slog"
	"math/rand"
	"os"
	"sync"
	"time"

	"github.com/go-faker/faker/v4"

	_ "github.com/mattn/go-sqlite3"
	"github.com/redis/go-redis/v9"
)

type LogProducer struct {
	AppName    string `json:"appName" faker:"oneof: user-service, order-service, payment-service, notification-service, analytics-service"`
	InstanceId string `json:"instanceId" faker:"oneof: 192.168.1.10, 192.168.1.11, 192.168.1.12, 10.0.0.5, 10.0.0.6"`
}

type LogLine struct {
	ID        int64        `json:"id" faker:"-"`
	Level     string       `json:"level" faker:"oneof: DEBUG, INFO, WARN, ERROR"`
	Message   string       `json:"message" faker:"sentence"`
	Timestamp string       `json:"timestamp" faker:"-"`
	App       *LogProducer `json:"app" faker:"-"`
}

type Worker struct {
	id       int
	rbd      *redis.Client
	db       *sql.DB
	logPool  *sync.Pool
	jsonPool *sync.Pool
}

func cleanup(ctx context.Context, db *sql.DB) {
	ticker := time.NewTicker(3 * time.Minute)
	defer ticker.Stop()
	for {
		select {
		case <-ctx.Done():
			return
		case <-ticker.C:
			db.Exec("delete from log_line where id not in (select id from log_line order by id desc limit 10000);")
			slog.Info("Cleanup done")
		}
	}
}

func (w *Worker) Run(ctx context.Context) error {
	i := 0
	for {
		// Get LogLine from pool
		logLine := w.logPool.Get().(*LogLine)

		err := faker.FakeData(logLine)
		err = faker.FakeData(logLine.App)
		if err != nil {
			log.Fatal(err)
		}

		now := time.Now()
		sqliteTimestamp := now.Format("2006-01-02 15:04:05.000")

		// Ensure LogProducer exists
		_, err = w.db.Exec("insert or ignore into log_producer (app_name, instance_id) values (?, ?)",
			logLine.App.AppName, logLine.App.InstanceId)
		if err != nil {
			log.Fatal(err)
		}

		result, err := w.db.Exec("insert into log_line (level, message, timestamp, app_name, instance_id) values (?, ?, ?, ?, ?)",
			logLine.Level, logLine.Message, sqliteTimestamp, logLine.App.AppName, logLine.App.InstanceId)
		if err != nil {
			log.Fatal(err)
		}

		id, err := result.LastInsertId()
		if err != nil {
			log.Fatal(err)
		}
		logLine.ID = id
		logLine.Timestamp = now.Format("2006-01-02T15:04:05.000")

		buf := w.jsonPool.Get().(*[]byte)
		*buf = (*buf)[:0]
		jsonData, err := json.Marshal(logLine)
		if err != nil {
			log.Fatal(err)
		}

		res := w.rbd.Publish(ctx, "logs", jsonData)
		if res.Err() != nil {
			log.Fatal(res.Err())
		}
		i = (i + 1) % 100
		if i == 0 {
			log.Printf("%d - published ID=%d: %s, appName=%s, instanceId=%s", w.id, logLine.ID, logLine.Message, logLine.App.AppName, logLine.App.InstanceId)
		}

		w.logPool.Put(logLine)
		w.jsonPool.Put(buf)

		time.Sleep(time.Second)
	}

}

func main() {
	dbPath := os.Getenv("DB_PATH")
	db, err := sql.Open("sqlite3", dbPath+"?_busy_timeout=5000&_journal_mode=WAL")
	if err != nil {
		log.Fatal(err)
	}
	redisURL := os.Getenv("REDIS_URL")
	rdb := redis.NewClient(&redis.Options{
		Addr:            redisURL,
		DB:              0,
		Password:        "",
		MaxRetries:      15,
		MinRetryBackoff: 100 * time.Millisecond,
		MaxRetryBackoff: 1 * time.Second,
	})
	rdb.Ping(context.Background())

	defer db.Close()

	logPool := &sync.Pool{
		New: func() any {
			return &LogLine{
				App: &LogProducer{},
			}
		},
	}

	jsonPool := &sync.Pool{
		New: func() any {
			buf := make([]byte, 0, 256)
			return &buf
		},
	}

	wg := new(sync.WaitGroup)
	for i := range 15 {
		wg.Go(func() {
			w := &Worker{
				id:       i,
				rbd:      rdb,
				db:       db,
				logPool:  logPool,
				jsonPool: jsonPool,
			}
			randomWaitTime := time.Duration(rand.Intn(1000)) * time.Millisecond
			time.Sleep(randomWaitTime)
			err := w.Run(context.Background())
			if err != nil {
				log.Fatal(err)
			}
		})
	}
	go cleanup(context.Background(), db)
	wg.Wait()

}
