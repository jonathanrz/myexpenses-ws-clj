(ns myexpenses-ws-clj.source
  (:use ring.util.response)
  (:require [clojure.java.jdbc :as sql]
            [monger.collection :as mc]
            [clojure.tools.logging :as log]
            [clojure.data.json :as json]
            [myexpenses-ws-clj.db :as db])
)

(defn table [] "sources")

(defn uuid [] (str (java.util.UUID/randomUUID)))

(defn get-all-sources []
  (response (json/write-str (mc/find-maps (db/get-db) (table))))
)

(defn get-source [id]
  (response (json/write-str (mc/find-maps (db/get-db) (table) {:_id id})))
)

(defn create-new-source [src]
  (response (mc/insert-and-return (db/get-db) (table) (merge src {:_id (uuid)})))
)

(defn update-source [id src]
    (mc/update (db/get-db) (table) {:_id id} src {:upsert true})
    (get-source id)
)

(defn delete-source [id]
  (mc/remove-by-id (db/get-db) (table) id)
  {:status 204}
)
