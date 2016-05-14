(ns myexpenses-ws-clj.mongo
  (:require [monger.core :as mg])
  (:import [com.mongodb MongoOptions ServerAddress]))

;; localhost, default port
(let [conn (mg/connect)])

;; given host, default port
(let [conn (mg/connect {:host "192.168.99.100"})])


;; given host, given port
(let [conn (mg/connect {:host "192.168.99.100" :port 27017})])

;; using MongoOptions allows fine-tuning connection parameters,
;; like automatic reconnection (highly recommended for production environment)
(let [^MongoOptions opts (mg/mongo-options {:threads-allowed-to-block-for-connection-multiplier 300})
      ^ServerAddress sa  (mg/server-address "192.168.99.100" 27017)
      conn               (mg/connect sa opts)]
  )
