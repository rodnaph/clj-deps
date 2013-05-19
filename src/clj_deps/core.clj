
(ns clj-deps.core
  (:require [clj-deps.fetcher :refer [dep->latest]]
            [clojure.java.io :as io]
            [clojure.edn :as edn]
            [clojure.xml :as xml]
            [boxuk.versions :refer [later-version?]])
  (:import (java.io PushbackReader)))

(defmulti fetch-project :source)

(defmulti project-url :source)

;; Github
;; ------

(defmethod project-url :github
  [project]
  (format "https://raw.github.com/%s/%s/project.clj"
          (:name project)
          (get project :branch "master")))

(defmethod fetch-project :github
  [project]
  (edn/read
   (PushbackReader.
     (io/reader
       (project-url project)))))

;; Version Fetching
;; ----------------

(defn project-map [project]
  (apply
    hash-map
    (drop 3 (fetch-project project))))

(defn with-latest-version [dependency]
  (conj dependency (dep->latest dependency)))

(defn out-of-date?
  "Indicates if the dependency is out of date (eg. [foo '1.2' '1.3'])"
  [dependency]
  (apply
    later-version?
    (drop 1 dependency)))

(defn check-dependencies [project]
  (->> project
       (project-map)
       :dependencies
       (map with-latest-version)
       (filter out-of-date?)))

