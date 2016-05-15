(ns myexpenses-ws-clj.db
  (:require [monger.core :as mg])
)

(defn get-db []
  (mg/get-db (mg/connect {:host "192.168.99.100:27017"}) "my-expenses-db")
)
