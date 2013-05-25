
(ns clj-deps.html
  (:require [net.cgrand.enlive-html :refer :all]
            [boxuk.versions :refer [later-version? latest-version latest-stable]]))

(defn status-class [current versions]
  (fn [node]
    (assoc-in
      node
      [:attrs :class]
      (cond
        (later-version? current (latest-stable versions)) "outdated"
        (later-version? current (latest-version versions)) "stable"
        :else "uptodate"))))

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
  [:p] (content (:description project))
  [:tbody :tr] (clone-for [[dep-name current versions] (:dependencies project)]
                          [:tr] (status-class current versions)
                          [:.name] (content (str dep-name))
                          [:.using] (content current)
                          [:.unstable] (content (latest-version versions))
                          [:.stable] (content (latest-stable versions))))

;; Public
;; ------

(defn project-show [project]
  (layout
    (:name project)
    (tpl-project-show project)))

