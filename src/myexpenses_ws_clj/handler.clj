(ns myexpenses-ws-clj.handler
      (:use compojure.core)
      (:use myexpenses-ws-clj.source)
      (:require [compojure.handler :as handler]
                [compojure.handler :refer [site]]
                [ring.middleware.json :as middleware]
                [compojure.route :as route]
                [ring.adapter.jetty :as jetty]))

    (defroutes app-routes
      (context "/sources" [] (defroutes sources-routes
        (GET  "/" [last-updated-at] (get-all-sources last-updated-at))
        (POST "/" {body :body} (create-new-source body))
        (context "/:id" [id] (defroutes source-routes
          (GET    "/" [] (get-source id))
          (PUT    "/" {body :body} (update-source id body))
          (DELETE "/" [] (delete-source id))))))
      (route/not-found "Not Found"))

    (def app
        (-> (handler/api app-routes)
            (middleware/wrap-json-body)
            (middleware/wrap-json-response)))

    (defn -main []
        (let [port (Integer/parseInt (get (System/getenv) "PORT" "3000"))]
          (jetty/run-jetty app {:port port})))
