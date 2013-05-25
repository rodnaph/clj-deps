
(ns clj-deps.project
  (:require [clj-deps.cache :refer [with-cache]]
            [clojure.edn :as edn]
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

(defn- project->edn
  "Fetch a project.clj and parse it to EDN."
  [project]
  (let [url (project-url project)]
    (with-cache (format "prj::%s" url)
      (edn/read
        (PushbackReader.
          (io/reader url))))))

;; Public
;; ------

(defn description->project
  "Turns a project spec into its project definition fetched from source control."
  [project]
  (let [data (project->edn project)]
    (merge project
           {:name (str (nth data 1))
            :version (nth data 2)}
           (apply
             hash-map
             (drop 3 data)))))

