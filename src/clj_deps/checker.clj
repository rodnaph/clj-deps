
(ns clj-deps.checker
  (:require [boxuk.versions :refer [later-version? latest-version latest-stable]]))

(defn- filter-versions
  ([filterer [dep-name current versions]]
   [dep-name
    current
    (filter filterer versions)]))

(defn- map-versions [f col]
  (map (fn [[x y z]]
         [x y (f z)]) col))

(defn- out-of-date? [[_ current latest]]
  (later-version? current latest))

(defn- out-dated [dependencies f]
  (->> dependencies
       (map-versions f)
       (filter out-of-date?)))

;; Public
;; ------

(defn check-dependencies
  "Return a projects out-dated dependencies"
  [project]
  (let [dependencies (:dependencies project)]
    {:stable (out-dated dependencies latest-stable)
     :unstable (out-dated dependencies latest-version) }))

