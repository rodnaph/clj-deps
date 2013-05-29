
(ns clj-deps.merger)

(defn- profile-dependencies [project]
  (mapcat
    (fn [[_ {:keys [dependencies]}]]
      dependencies)
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
      (:dependencies project)
      (:dev-dependencies project)
      (:plugins project)
      (profile-dependencies project))))

