
(ns clj-deps.project
  (:require [clj-deps.cache :refer [with-cache]]
            [clojure.edn :as edn]
            [clojure.java.io :as io])
  (:import (java.io PushbackReader)))

(defn- from-domain [domain project]
  (format "https://%s/%s/%s/"
          domain
          (:user project)
          (:repo project)))

(defmulti project-url :source)

(defmulti project-clj-url :source)

;; Github

(defmethod project-url :github
  [project]
  (from-domain "github.com" project))

(defmethod project-clj-url :github
  [project]
  (format "https://raw.github.com/%s/%s/%s/project.clj"
          (:user project)
          (:repo project)
          (get project :branch "master")))

;; BitBucket

(defmethod project-url :bitbucket
  [project]
  (from-domain "bitbucket.org" project))

(defmethod project-clj-url :bitbucket
  [project]
  (format "https://bitbucket.org/%s/%s/raw/%s/project.clj"
          (:user project)
          (:repo project)
          (get project :branch "master")))

;; Fns

(defn- project->edn
  "Fetch a project.clj and parse it to EDN."
  [project]
  (let [url (project-clj-url project)]
    (with-cache (format "prj::%s" url)
      (edn/read
        (PushbackReader.
          (io/reader url))))))

;; Public
;; ------

(defn description->project
  "Turns a project spec into its project definition fetched from source control."
  [project]
  (let [[_ project-name version & info] (project->edn project)]
    (merge project
           {:name (str project-name)
            :version version}
           (apply hash-map info))))

