
(ns clj-deps.checker
  (:require [boxuk.versions :refer [later-version? latest-version latest-stable]]))

(defn- out-dated
  "Return outdated dependencies after filtering their versions by f"
  [dependencies f]
  (filter #(later-version?
             (nth % 1)
             (f (nth % 2)))
          dependencies))

;; Public
;; ------

(defn project->status
  "Return a projects out-dated dependencies"
  [project]
  (let [dependencies (:all-dependencies project)]
    (merge project
      {:stable (out-dated dependencies latest-stable)
       :unstable (out-dated dependencies latest-version)})))

