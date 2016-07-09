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
                [myexpenses-ws-clj.receipt :as receipt]
                [clojure.tools.logging :as log]))

    (defroutes app-routes
      (context "/sources" [] (defroutes sources-routes
        (GET  "/" [last-updated-at] (source/get-all last-updated-at))
        (POST "/" {body :body} (source/create-new body))
        (context "/:id" [id] (defroutes source-routes
          (GET    "/" [] (source/get id))
          (PATCH  "/" {body :body} (source/update-partial id body))
          (PUT    "/" {body :body} (source/update-complete id body))
          (DELETE "/" [] (source/delete id))))))
      (context "/accounts" [] (defroutes accounts-routes
        (GET  "/" [last-updated-at] (account/get-all last-updated-at))
        (POST "/" {body :body} (account/create-new body))
        (context "/:id" [id] (defroutes account-routes
          (GET    "/" [] (account/get id))
          (PATCH  "/" {body :body} (account/update-partial id body))
          (PUT    "/" {body :body} (account/update-complete id body))
          (DELETE "/" [] (account/delete id))))))
      (context "/bills" [] (defroutes bills-routes
        (GET  "/" [last-updated-at] (bill/get-all last-updated-at))
        (POST "/" {body :body} (bill/create-new body))
        (context "/:id" [id] (defroutes bill-routes
          (GET    "/" [] (bill/get id))
          (PATCH  "/" {body :body} (bill/update-partial id body))
          (PUT    "/" {body :body} (bill/update-complete id body))
          (DELETE "/" [] (bill/delete id))))))
      (context "/cards" [] (defroutes cards-routes
        (GET  "/" [last-updated-at] (card/get-all last-updated-at))
        (POST "/" {body :body} (card/create-new body))
        (context "/:id" [id] (defroutes card-routes
          (GET    "/" [] (card/get id))
          (PATCH  "/" {body :body} (card/update-partial id body))
          (PUT    "/" {body :body} (card/update-complete id body))
          (DELETE "/" [] (card/delete id))))))
      (context "/expenses" [] (defroutes expenses-routes
        (GET  "/" [last-updated-at] (expense/get-all last-updated-at))
        (POST "/" {body :body} (expense/create-new body))
        (context "/:id" [id] (defroutes expense-routes
          (GET    "/" [] (expense/get id))
          (PATCH  "/" {body :body} (expense/update-partial id body))
          (PUT    "/" {body :body} (expense/update-complete id body))
          (DELETE "/" [] (expense/delete id))))))
      (context "/receipts" [] (defroutes receipts-routes
        (GET  "/" [last-updated-at] (receipt/get-all last-updated-at))
        (POST "/" {body :body} (receipt/create-new body))
        (context "/:id" [id] (defroutes receipt-routes
          (GET    "/" [] (receipt/get id))
          (PATCH  "/" {body :body} (receipt/update-partial id body))
          (PUT    "/" {body :body} (receipt/update-complete id body))
          (DELETE "/" [] (receipt/delete id))))))
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
          (jetty/run-jetty app {:port port :max-threads 10})))
