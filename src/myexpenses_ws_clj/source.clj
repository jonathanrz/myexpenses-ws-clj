(ns myexpenses-ws-clj.source
  (:use ring.util.response)
  (:use myexpenses-ws-clj.server)
  (:use myexpenses-ws-clj.db)
  (:require [clojure.java.jdbc :as sql]
            [monger.collection :as mc]
            [monger.core :as mg]
            [monger.credentials :as mcr]
            [clojure.tools.logging :as log]
            [clojure.data.json :as json])
  (:import org.bson.types.ObjectId))

(sql/with-connection (db-connection)
;  (sql/drop-table :documents) ; no need to do that for in-memory databases
  (sql/create-table :sources [:id "varchar(256)" "primary key"]
                             [:name "varchar(1024)"]))

(defn uuid [] (str (java.util.UUID/randomUUID)))

; (defn get-db []
;   (let [db   "my-expenses-debug"
;         u    "jonathanrz"
;         p    "ZaqLgME79HTAAR"]
;       (mg/connect-with-credentials "192.168.99.100" (mcr/create u db p))
;   )
; )

(defn get-db []
  (mg/get-db (mg/connect {:host "192.168.99.100:27017"}) "my-expenses-db")
)

(defn format-source-as-response [source]
  (log/info "source=" source)
  {
    :id (get source :_id)
    :name (get source :name)
    :test "a"
  }
)

(defn get-all-sources []
  (let [conn (mg/connect {:host "192.168.99.100:27017"})
        db   (mg/get-db conn "my-expenses-db")
        sources (mc/find-maps db "sources")]
        (response (json/write-str sources))
  )
)

(defn get-source [id]
  (sql/with-connection (db-connection)
    (sql/with-query-results results
      ["select * from sources where id = ?" id]
      (cond
        (empty? results) {:status 404}
        :else (response (first results))))))

(defn create-new-source [src]
  (let [conn (mg/connect {:host "192.168.99.100:27017"})
        db   (mg/get-db conn "my-expenses-db")
        id   (uuid)]
        (response (mc/insert-and-return db "sources" (merge src {:_id id})))
  )
)

(defn update-source [id src]
    (sql/with-connection (db-connection)
      (let [source (assoc src "id" id)]
        (sql/update-values :sources ["id=?" id] source)))
    (get-source id))

(defn delete-source [id]
  (sql/with-connection (db-connection)
    (sql/delete-rows :sources ["id=?" id]))
  {:status 204})
