(ns myexpenses-ws-clj.db
  (:require [monger.core :as mg]
            [clojure.tools.logging :as log])
)

(defn get-db-url []
  (get (System/getenv) "MONGODB_URI" "192.168.99.100:27017"))

(defn get-db [
  let {:keys [conn db]} (mg/connect-via-uri {:host (get-db-url)})]
  (mg/get-db conn "my-expenses-db")
)
