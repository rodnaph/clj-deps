
(ns clj-deps.core
  (:require [clj-deps.fetcher :refer [dep->versions]]
            [clojure.java.io :as io]
            [clojure.edn :as edn]
            [clojure.xml :as xml]
            [boxuk.versions :refer [stable? latest-version]])
  (:import (java.io PushbackReader)))

(defmulti project-url :source)

(defmethod project-url :github
  [project]
  (format "https://raw.github.com/%s/%s/project.clj"
          (:name project)
          (get project :branch "master")))

(defmethod project-url :bitbucket
  [project]
  (format "https://bitbucket.org/%s/raw/%s/project.clj"
          (:name project)
          (get project :branch "master")))

(defn project->edn
  "Fetch a project.clj and parse it to EDN."
  [project]
  (edn/read
    (PushbackReader.
      (io/reader
        (project-url project)))))

;; Version Fetching
;; ----------------

(defn project-map
  "Turns a project spec into its project definition fetched
  from source control."
  [project]
  (apply
    hash-map
    (drop 3 (project->edn project))))

(defn with-versions
  "Adds the latest version to the dependency vector.
  [foo '1.2.3'] => [foo '1.2.3' ['1.2.4' '1.2.3' '1.2.2']]"
  [dependency]
  (conj dependency
        (dep->versions dependency)))

(defn- filter-versions
  ([filterer [dep-name current versions]]
   [dep-name
    current
    (filter filterer versions)]))

(defn out-of-date-deps
  "Return a projects out-dated dependencies"
  [project]
  (let [deps (->> project
                  (project-map)
                  :dependencies
                  (map with-versions))]
    {:stable (map (partial filter-versions stable?) deps)
     :unstable (map (partial filter-versions identity) deps)}))

(def project
  {:source :github
   :name "rodnaph/diallo"
   :branch "master"})

(out-of-date-deps project)

