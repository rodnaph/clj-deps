
(ns clj-deps.web
  (:require [clj-deps.project :refer [description->project]]
            [clj-deps.maven :refer [project->versions]]
            [clj-deps.checker :refer [project->status]]
            [clj-deps.html :as html]
            [router.core :refer [set-routes! url rte]]
            [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.util.response :refer [file-response redirect]]
            [clojure.string :as s]))

(set-routes!
  {:home              "/"
   :lookup            "/lookup"
   :project           "/:source/:user/:repo"
   :project.badge     "/:source/:user/:repo/status.png"})

(defn wrap-exception [handler]
  (fn [req]
    (try
      (handler req)
      (catch Exception e
        {:body (html/exception)}))))

(defn req->description [{:keys [params]}]
  (let [{:keys [source user repo]} params]
    {:source (keyword source)
     :user user
     :repo repo
     :name (format "%s/%s" user repo)}))

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

(defn www-index [req]
  (html/index-show))

(defn www-project [req]
  (html/project-show
    (req->status req)))

(defn www-not-found [req]
  (html/not-found))

(defn lookup-project [{:keys [params]}]
  (let [[user repo] (s/split (:name params) #"/")
        url (url :project
                 :source (:source params)
                 :user user
                 :repo repo)]
    (redirect url)))

(defroutes all-routes
  (route/resources "/assets")
  (GET (rte :home) [] www-index)
  (GET (rte :lookup) [] lookup-project)
  (GET (rte :project) [] www-project)
  (GET (rte :project.badge) [] (partial png-for :stable))
  (GET "/:source/:repo/:user/unstable.png" [] (partial png-for :unstable))
  (route/not-found www-not-found))

(def app
  (-> #'all-routes
      (wrap-exception)
      (handler/site)))

