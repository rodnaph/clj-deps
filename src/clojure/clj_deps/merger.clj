
(ns clj-deps.merger)

(defn- extract [project]
  (concat
    (:dependencies project)
    (:dev-dependencies project)
    (:plugins project)))

(defn- profile-dependencies [project]
  (mapcat
    (comp extract second)
    (:profiles project)))

;; Public
;; ------

(defn merge-dependencies
  "Merge :dependencies, :dev-dependencies, :plugins, etc... into :all-dependencies"
  [project]
  (assoc
    project
    :all-dependencies
    (concat
      (extract project)
      (profile-dependencies project))))

