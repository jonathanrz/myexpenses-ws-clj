(ns myexpenses-ws-clj.source
  (:use ring.util.response)
  (:require [clojure.java.jdbc :as sql]
            [monger.collection :as mc]
            [clojure.tools.logging :as log]
            [clojure.data.json :as json]
            [myexpenses-ws-clj.db :as db]
            [clj-time.core :as time]
            [clj-time.coerce :as tc]
            [monger.operators :refer :all]))

(defn table [] "sources")

(defn uuid [] (str (java.util.UUID/randomUUID)))

(defn now [] (tc/to-long (time/now)))

(defn get-all-sources [last-updated-at]
  (response (json/write-str (mc/find-maps (db/get-db) (table) {:updated_at { $gt (read-string last-updated-at) }})))
)

(defn get-source [id]
  (response (json/write-str (mc/find-maps (db/get-db) (table) {:_id id})))
)

(defn create-new-source [src]
  (let [id (uuid)]
    (mc/insert (db/get-db) (table) (merge src {:_id id :created_at (now) :updated_at (now)}))
    (get-source id)
  )
)

(defn update-source [id src]
  (let [origin (into {} (mc/find-maps (db/get-db) (table) {:_id id}))
        new (merge origin src)]
    (log/info "origin=" origin)
    (log/info "new=" new)
    (mc/update (db/get-db) (table) {:_id id} (merge new {:updatedAt (now)}) {:upsert false})
    (get-source id)
  )
)

(defn delete-source [id]
  (mc/remove-by-id (db/get-db) (table) id)
  {:status 204}
)
