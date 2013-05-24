
(ns clj-deps.html
  (:require [net.cgrand.enlive-html :refer :all]
            [boxuk.versions :refer [latest-version latest-stable]]))

(deftemplate layout "index.html"
  [title & body]
  [:title] (content (format "CljDeps: %s" title))
  [:content] (substitute body))

(defsnippet tpl-project-show
  "index.html" [:.project-show]
  [project]
  [:h2] (content (:name project))
  [:.dep] (clone-for [[dep-name current versions] (:dependencies project)]
                     [:h3] (content (str dep-name))
                     [:.current] (content current)
                     [:.unstable] (content (latest-version versions))
                     [:.stable] (content (latest-stable versions))))

;; Public
;; ------

(defn project-show [project]
  (layout
    (:name project)
    (tpl-project-show project)))

