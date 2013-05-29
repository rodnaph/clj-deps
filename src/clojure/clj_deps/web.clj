
(ns clj-deps.web
  (:require [clj-deps.project :refer [description->project]]
            [clj-deps.merger :refer [merge-dependencies]]
            [clj-deps.maven :refer [project->versions]]
            [clj-deps.checker :refer [project->status]]
            [clj-deps.html :as html]
            [clj-deps.util :refer [FIVE_MINUTES_IN_SECS]]
            [router.core :refer [set-routes! url rte]]
            [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.util.response :refer [file-response redirect header]]
            [ring.middleware.stacktrace :refer [wrap-stacktrace]]
            [clojure.string :as s]))

(set-routes!
  {:home              "/"
   :lookup            "/lookup"
   :project           "/:source/:user/:repo"
   :project.badge     "/:source/:user/:repo/status.png"})

(defn- wrap-exception [handler]
  (fn [req]
    (try
      (handler req)
      (catch Exception e
        {:body (html/exception)}))))

(defn- req->description [{:keys [params]}]
  (let [{:keys [source user repo]} params]
    {:source (keyword source)
     :user user
     :repo repo}))

(defn- req->status [req]
  (-> (req->description req)
      (description->project)
      (merge-dependencies)
      (project->versions)
      (project->status)))

(defn- png-for [stability req]
  (let [filename (-> (req->status req)
                     (stability)
                     (empty?)
                     (if "uptodate" "outdated"))
        resource (format "resources/images/%s.png" filename)]
    (-> resource
        (file-response)
        (header "cache-control"
                (format "public, max-age=%d"
                        FIVE_MINUTES_IN_SECS)))))

(defn- www-index [req]
  (html/index-show))

(defn- www-project [req]
  (html/project-show
    (req->status req)))

(defn- www-not-found [req]
  (html/not-found))

(defn- lookup-project [{:keys [params]}]
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

;; Public
;; ------

(def dev-app
  (-> #'all-routes
      (wrap-stacktrace)
      (handler/site)))

(def prod-app
  (-> #'all-routes
      (wrap-exception)
      (handler/site)))

