
(ns clj-deps.web
  (:require [clj-deps.project :refer [description->project]]
            [clj-deps.maven :refer [project->versions]]
            [clj-deps.checker :refer [project->status]]
            [net.cgrand.enlive-html :refer :all]
            [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.util.response :refer [file-response]]
            [ring.middleware.stacktrace :refer [wrap-stacktrace]]))

(defn req->description [{:keys [params]}]
  {:source :github
   :name (format "%s/%s"
                 (:repo params)
                 (:user params))})

(defn png-for [stability req]
  (let [status (-> (req->description req)
                   (description->project)
                   (project->versions)
                   (project->status))
        result (if (empty? (stability status))
                 "uptodate"
                 "outdated")]
    (file-response
      (format "resources/images/%s.png" result))))

(deftemplate www-index "index.html"
  [req])

(defroutes all-routes
  (GET "/" [] www-index)
  (GET "/:repo/:user/stable.png" [] (partial png-for :stable))
  (GET "/:repo/:user/unstable.png" [] (partial png-for :unstable))
  (route/resources "/assets")
  (route/not-found "404"))

(def app
  (-> #'all-routes
      (wrap-stacktrace)
      (handler/site)))

