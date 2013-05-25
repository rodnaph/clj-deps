
(ns clj-deps.html
  (:require [net.cgrand.enlive-html :refer :all]
            [router.core :refer [action src]]
            [boxuk.versions :refer [later-version? latest-version latest-stable]]))

(defn markdown [{:keys [source user repo]}]
  (let [url (format "%s/%s/%s"
                    (name source)
                    user
                    repo)]
    (format "[![Build Status](http://clj-deps.herokuapp.com/%s/status.png)](http://clj-deps.herokuapp.com/%s)"
          url url)))

(defn status-class [current versions]
  (fn [node]
    (assoc-in
      node
      [:attrs :class]
      (cond
        (later-version? current (latest-stable versions)) "status outdated"
        (later-version? current (latest-version versions)) "status"
        :else "status uptodate"))))

(deftemplate layout "index.html"
  [title & body]
  [:title] (content (format "CljDeps: %s" title))
  [:content] (substitute body))

(defsnippet tpl-project-show
  "index.html" [:.project-show]
  [project]
  [:h2] (content (format "%s %s"
                         (:name project)
                         (:version project)))
  [:.desc] (content (:description project))
  [:.badge-stable] (src :project.badge
                        :source (name (:source project))
                        :user (:user project)
                        :repo (:repo project))
  [:.markdown] (content (markdown project))
  [:tbody :tr] (clone-for [[dep-name current versions] (:dependencies project)]
                          [:.status] (status-class current versions)
                          [:.name] (content (str dep-name))
                          [:.using] (content current)
                          [:.unstable] (content (latest-version versions))
                          [:.stable] (content (latest-stable versions))))

(defsnippet tpl-index-show
  "index.html" [:.index-show]
  []
  [:form] (action :lookup))

(defsnippet tpl-not-found
  "index.html" [:.not-found]
  [])

(defsnippet tpl-exception
  "index.html" [:.exception]
  [])

;; Public
;; ------

(defn index-show []
  (layout
    "Clojure & ClojureScript Dependencies"
    (tpl-index-show)))

(defn project-show [project]
  (layout
    (:name project)
    (tpl-project-show project)))

(defn not-found []
  (layout
    "This is not the page you're looking for..."
    (tpl-not-found)))

(defn exception []
  (layout
    "Ooooops...."
    (tpl-exception)))

