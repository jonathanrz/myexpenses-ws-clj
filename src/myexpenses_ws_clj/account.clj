(ns myexpenses-ws-clj.account
  (:use ring.util.response)
  (:require [clojure.java.jdbc :as sql]
            [monger.collection :as mc]
            [clojure.tools.logging :as log]
            [clojure.data.json :as json]
            [myexpenses-ws-clj.db :as db]
            [clj-time.core :as time]
            [clj-time.coerce :as tc]
            [monger.operators :refer :all]))

(def table "accounts")

(defn uuid [] (str (java.util.UUID/randomUUID)))

(defn now [] (tc/to-long (time/now)))

(defn get-all [last-updated-at]
  (response (json/write-str (mc/find-maps (db/get-db) table {:updated_at { $gt (read-string last-updated-at) }})))
)

(defn get [id]
  (response (json/write-str (mc/find-maps (db/get-db) table {:_id id})))
)

(defn create-new [src]
  (let [id (uuid)]
    (mc/insert (db/get-db) table (merge src {:_id id :created_at (now) :updated_at (now)}))
    (get id)
  )
)

(defn update [id src]
  (let [origin (into {} (mc/find-maps (db/get-db) (table) {:_id id}))
        new (merge origin src)]
    (mc/update (db/get-db) table {:_id id} (merge new {:updatedAt (now)}) {:upsert false})
    (get id)
  )
)

(defn delete [id]
  (mc/remove-by-id (db/get-db) table id)
  {:status 204}
)
