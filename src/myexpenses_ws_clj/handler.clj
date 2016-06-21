(ns myexpenses-ws-clj.handler
      (:use compojure.core)
      (:require [compojure.handler :as handler]
                [compojure.handler :refer [site]]
                [ring.middleware.json :as middleware]
                [compojure.route :as route]
                [ring.adapter.jetty :as jetty]
                [myexpenses-ws-clj.account :as account]
                [myexpenses-ws-clj.source :as source]))

    (defroutes app-routes
      (context "/sources" [] (defroutes sources-routes
        (GET  "/" [last-updated-at] (source/get-all last-updated-at))
        (POST "/" {body :body} (source/create-new body))
        (context "/:id" [id] (defroutes source-routes
          (GET    "/" [] (source/get id))
          (PUT    "/" {body :body} (source/update id body))
          (DELETE "/" [] (source/delete id))))))
      (context "/accounts" [] (defroutes accounts-routes
        (GET  "/" [last-updated-at] (account/get-all last-updated-at))
        (POST "/" {body :body} (account/create-new body))
        (context "/:id" [id] (defroutes account-routes
          (GET    "/" [] (account/get id))
          (PUT    "/" {body :body} (account/update id body))
          (DELETE "/" [] (account/delete id))))))
      (route/not-found "Not Found"))

    (def app
        (-> (handler/api app-routes)
            (middleware/wrap-json-body)
            (middleware/wrap-json-response)))

    (defn -main []
        (let [port (Integer/parseInt (get (System/getenv) "PORT" "3000"))]
          (jetty/run-jetty app {:port port})))
