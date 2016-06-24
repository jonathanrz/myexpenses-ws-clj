(ns myexpenses-ws-clj.handler
      (:use compojure.core)
      (:use ring.util.response)
      (:require [compojure.handler :as handler]
                [ring.middleware.json :as middleware]
                [compojure.route :as route]
                [ring.adapter.jetty :as jetty]
                [myexpenses-ws-clj.account :as account]
                [myexpenses-ws-clj.source :as source]
                [myexpenses-ws-clj.bill :as bill]
                [myexpenses-ws-clj.card :as card]
                [myexpenses-ws-clj.expense :as expense]
                [clojure.tools.logging :as log]))

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
      (context "/bills" [] (defroutes bills-routes
        (GET  "/" [last-updated-at] (bill/get-all last-updated-at))
        (POST "/" {body :body} (bill/create-new body))
        (context "/:id" [id] (defroutes bill-routes
          (GET    "/" [] (bill/get id))
          (PUT    "/" {body :body} (bill/update id body))
          (DELETE "/" [] (bill/delete id))))))
      (context "/cards" [] (defroutes cards-routes
        (GET  "/" [last-updated-at] (card/get-all last-updated-at))
        (POST "/" {body :body} (card/create-new body))
        (context "/:id" [id] (defroutes card-routes
          (GET    "/" [] (card/get id))
          (PUT    "/" {body :body} (card/update id body))
          (DELETE "/" [] (card/delete id))))))
      (context "/expenses" [] (defroutes expenses-routes
        (GET  "/" [last-updated-at] (expense/get-all last-updated-at))
        (POST "/" {body :body} (expense/create-new body))
        (context "/:id" [id] (defroutes expense-routes
          (GET    "/" [] (expense/get id))
          (PUT    "/" {body :body} (expense/update id body))
          (DELETE "/" [] (expense/delete id))))))
      (route/not-found "Not Found"))

    (defn check-auth-header [handler]
      (fn [request]
        (if (= (get (request :headers) "auth-token") (get (System/getenv) "MYEXPENSES_AUTH_TOKEN"))
          (handler request)
          {:status 401}
        )
      )
    )

    (def app
        (-> (handler/api app-routes)
            (middleware/wrap-json-body)
            (middleware/wrap-json-response)
            (check-auth-header)))

    (defn -main []
        (let [port (Integer/parseInt (get (System/getenv) "PORT" "3000"))]
          (jetty/run-jetty app {:port port})))
