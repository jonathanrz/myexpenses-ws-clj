(ns myexpenses-ws-clj.card
  (:use ring.util.response)
  (:use myexpenses-ws-clj.helper)
  (:require [clojure.java.jdbc :as sql]
            [monger.collection :as mc]
            [clojure.tools.logging :as log]
            [clojure.data.json :as json]
            [myexpenses-ws-clj.db :as db]
            [monger.operators :refer :all]))

(def table "cards")

(defn get-all [last-updated-at]
  (json-response (json/write-str (mc/find-maps (db/get-db) table {:updated_at { $gt (read-string last-updated-at) }})))
)

(defn find-source [id]
  (first (mc/find-maps (db/get-db) table {:_id id}))
)

(defn get [id]
  (json-response (json/write-str (find-source id)))
)

(defn create-new [src]
  (let [id (uuid)]
    (mc/insert (db/get-db) table (merge src {:_id id :created_at (now) :updated_at (now)}))
    (get id)
  )
)

(defn update [id src]
  (let [origin (into {} (find-source id))
        new (merge origin src)]
    (mc/update (db/get-db) table {:_id id} (merge new {:updatedAt (now)}) {:upsert false})
    (get id)
  )
)

(defn delete [id]
  (mc/remove-by-id (db/get-db) table id)
  {:status 204}
)
