
(ns clj-deps.web
  (:require [clj-deps.project :refer [description->project]]
            [clj-deps.maven :refer [project->versions]]
            [clj-deps.checker :refer [project->status]]
            [clj-deps.html :as html]
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

(defn req->status [req]
  (-> (req->description req)
      (description->project)
      (project->versions)
      (project->status)))

(defn png-for [stability req]
  (let [result (-> (req->status req)
                   (stability)
                   (empty?))]
    (file-response
      (format "resources/images/%s.png"
              (if result "uptodate" "outdated")))))

(deftemplate www-index "index.html"
  [req])

(defn www-project [req]
  (html/project-show
    (req->status req)))

(defroutes all-routes
  (GET "/" [] www-index)
  (GET "/:repo/:user" [] www-project)
  (GET "/:repo/:user/stable.png" [] (partial png-for :stable))
  (GET "/:repo/:user/unstable.png" [] (partial png-for :unstable))
  (route/resources "/assets")
  (route/not-found "404"))

(def app
  (-> #'all-routes
      (wrap-stacktrace)
      (handler/site)))

