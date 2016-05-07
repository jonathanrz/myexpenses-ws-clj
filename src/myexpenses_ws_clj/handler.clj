(ns myexpenses-ws-clj.handler
      (:use compojure.core)
      (:use myexpenses-ws-clj.source)
      (:require [compojure.handler :as handler]
                [ring.middleware.json :as middleware]
                [compojure.route :as route]))

    (defroutes app-routes
      (context "/sources" [] (defroutes sources-routes
        (GET  "/" [] (get-all-sources))
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
