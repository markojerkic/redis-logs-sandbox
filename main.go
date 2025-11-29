package main

import (
	"context"
	"database/sql"
	"encoding/json"
	"log"
	"os"
	"sync"
	"time"

	"github.com/go-faker/faker/v4"

	_ "github.com/mattn/go-sqlite3"
	"github.com/redis/go-redis/v9"
)

type LogLine struct {
	ID        int64  `json:"id" faker:"-"`
	Level     string `json:"level" faker:"oneof: DEBUG, INFO, WARN, ERROR"`
	Message   string `json:"message" faker:"sentence"`
	Timestamp string `json:"timestamp" faker:"-"`
}

type Worker struct {
	id  int
	rbd *redis.Client
	db  *sql.DB
}

func (w *Worker) Run(ctx context.Context) error {
	logLine := &LogLine{}

	for {
		err := faker.FakeData(logLine)
		if err != nil {
			log.Fatal(err)
		}

		// Generate ISO-8601 timestamp without timezone (for Java LocalDateTime)
		logLine.Timestamp = time.Now().Format("2006-01-02T15:04:05")

		// Insert and get the generated ID back
		result, err := w.db.Exec("insert into log_line (level, message, timestamp) values (?, ?, ?)", logLine.Level, logLine.Message, logLine.Timestamp)
		if err != nil {
			log.Fatal(err)
		}

		// Get the last inserted ID
		id, err := result.LastInsertId()
		if err != nil {
			log.Fatal(err)
		}
		logLine.ID = id

		// Publish the entire LogLine as JSON
		jsonData, err := json.Marshal(logLine)
		if err != nil {
			log.Fatal(err)
		}

		res := w.rbd.Publish(ctx, "logs", jsonData)
		if res.Err() != nil {
			log.Fatal(res.Err())
		}
		log.Printf("%d - published ID=%d: %s", w.id, logLine.ID, logLine.Message)

		// Add a small delay to reduce contention
		time.Sleep(100 * time.Millisecond)
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
		Addr:     redisURL,
		DB:       0,
		Password: "",
	})
	rdb.Ping(context.Background())

	defer db.Close()
	rows, err := db.Query("select * from log_line;")
	if err != nil {
		log.Fatal(err)
	}
	defer rows.Close()
	for rows.Next() {
		var id int
		var level string
		var message string
		var timestamp string
		err := rows.Scan(&id, &level, &message, &timestamp)
		if err != nil {
			log.Fatal(err)
		}
		log.Printf("[%s] %s - %s (%d)", timestamp, level, message, id)
	}

	wg := new(sync.WaitGroup)
	for i := range 30 {
		wg.Go(func() {
			w := &Worker{
				id:  i,
				rbd: rdb,
				db:  db,
			}
			err := w.Run(context.Background())
			if err != nil {
				log.Fatal(err)
			}
		})
	}
	wg.Wait()

}
