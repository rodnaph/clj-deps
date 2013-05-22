
(ns clj-deps.web
  (:require [clj-deps.project :refer [description->project]]
            [clj-deps.maven :refer [project->versions]]
            [clj-deps.checker :refer [project->status]]
            [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.util.response :refer [file-response]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.stacktrace :refer [wrap-stacktrace]]))

(defn req->name [{:keys [params]}]
  (format "%s/%s"
          (:repo params)
          (:user params)))

(defn png-for [stability req]
  (let [description {:source :github
                     :name (req->name req)}
        status (-> description
                   (description->project)
                   (project->versions)
                   (project->status))
        file (if (empty? (stability status))
               "uptodate"
               "outdated")]
    (file-response
      (format "resources/images/%s.png"
              file))))

(defroutes all-routes
  (GET "/" [] "Hello, World")
  (GET "/:repo/:user/stable.png" [] (partial png-for :stable))
  (GET "/:repo/:user/unstable.png" [] (partial png-for :unstable))
  (route/resources "/assets")
  (route/not-found "404"))

(def app
  (-> #'all-routes
      (wrap-reload)
      (wrap-stacktrace)
      (handler/site)))

