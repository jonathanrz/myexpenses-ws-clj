(ns myexpenses-ws-clj.helper
  (:require [clj-time.coerce :as tc]
            [clj-time.core :as time]))

(defn json-response [body]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body body
  }
)

(defn uuid [] (str (java.util.UUID/randomUUID)))

(defn now [] (tc/to-long (time/now)))
