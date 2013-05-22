
(ns clj-deps.web
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.stacktrace :refer [wrap-stacktrace]]))

(defn png-stable [req]
  "STABLE")

(defn png-unstable [req]
  "UNSTABLE")

(defroutes all-routes
  (GET "/" [] "Hello, World")
  (GET "/:repo/:user/stable.png" [] png-stable)
  (GET "/:repo/:user/unstable.png" [] png-stable)
  (route/resources "/assets")
  (route/not-found "404"))

(def app
  (-> #'all-routes
      (wrap-reload)
      (wrap-stacktrace)
      (handler/site)))

