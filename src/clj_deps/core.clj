
(ns clj-deps.core
  (:require [clj-deps.fetcher :refer [dep->latest]]
            [clojure.java.io :as io]
            [clojure.edn :as edn]
            [clojure.xml :as xml]
            [boxuk.versions :refer [later-version?]])
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

(defn with-latest-version
  "Adds the latest version to the dependency vector.
   [foo '1.2.3'] => [foo '1.2.3' '1.2.4']"
  [dependency]
  (conj dependency (dep->latest dependency)))

(defn out-of-date?
  "Indicates if the dependency is out of date (eg. [foo '1.2' '1.3'])"
  [dependency]
  (apply
    later-version?
    (drop 1 dependency)))

(defn out-of-date-deps
  "Return a projects out-dated dependencies"
  [project]
  (->> project
       (project-map)
       :dependencies
       (map with-latest-version)
       (filter out-of-date?)))

