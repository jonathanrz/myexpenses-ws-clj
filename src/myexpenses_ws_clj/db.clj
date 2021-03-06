(ns myexpenses-ws-clj.db
  (:require [monger.core :as mg]
            [clojure.tools.logging :as log])
)

(defn get-db-url []
  (get (System/getenv) "MONGODB_URI" "mongodb://192.168.99.100:27017/my-expenses-db"))

(defn get-db []
  (let [uri (get-db-url)
        {:keys [conn db]} (mg/connect-via-uri uri)]
  db)
)
