(ns myexpenses-ws-clj.db
  (:require [monger.core :as mg])
)

(defn get-db-url []
  (get (System/getenv) "MONGODB_URI" "192.168.99.100:27017"))

(defn get-db []
  (mg/get-db (mg/connect {:host (get-db-url)}) "my-expenses-db")
)
