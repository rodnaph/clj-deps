
(ns clj-deps.project
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io])
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

;; Public
;; ------

(defn project->map
  "Turns a project spec into its project definition fetched from source control."
  [project]
  (apply
    hash-map
    (drop 3 (project->edn project))))

