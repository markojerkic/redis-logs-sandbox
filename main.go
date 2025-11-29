package main

import (
	"database/sql"
	"log"
	"os"

	_ "github.com/mattn/go-sqlite3"
)

func main() {
	dbPath := os.Getenv("DB_PATH")
	db, err := sql.Open("sqlite3", dbPath)
	if err != nil {
		log.Fatal(err)
	}
	defer db.Close()
	rows, err := db.Query("select * from logs;")
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
}
