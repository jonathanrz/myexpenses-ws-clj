(ns myexpenses-ws-clj.source
  (:use ring.util.response)
  (:use myexpenses-ws-clj.db)
  (:require [clojure.java.jdbc :as sql]))

(sql/with-connection (db-connection)
;  (sql/drop-table :documents) ; no need to do that for in-memory databases
  (sql/create-table :sources [:id "varchar(256)" "primary key"]
                               [:name "varchar(1024)"]))

(defn uuid [] (str (java.util.UUID/randomUUID)))

(defn get-all-sources []
  (response
    (sql/with-connection (db-connection)
      (sql/with-query-results results
        ["select * from sources"]
        (into [] results)))))

(defn get-source [id]
  (sql/with-connection (db-connection)
    (sql/with-query-results results
      ["select * from sources where id = ?" id]
      (cond
        (empty? results) {:status 404}
        :else (response (first results))))))

(defn create-new-source [src]
  (let [id (uuid)]
    (sql/with-connection (db-connection)
      (let [source (assoc src "id" id)]
        (sql/insert-record :sources source)))
    (get-source id)))

(defn update-source [id src]
    (sql/with-connection (db-connection)
      (let [source (assoc src "id" id)]
        (sql/update-values :sources ["id=?" id] source)))
    (get-source id))

(defn delete-source [id]
  (sql/with-connection (db-connection)
    (sql/delete-rows :sources ["id=?" id]))
  {:status 204})
