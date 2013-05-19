
(ns clj-deps.core
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]
            [clojure.xml :as xml]
            [net.cgrand.enlive-html :refer :all])
  (:import (java.io PushbackReader)))

(def repositories
  "Standard repositories to check.  Extra repos per-project can be added."
  [{:name "Clojars"
    :url "http://clojars.org/repo"}
   {:name "Maven Central"
    :url "http://repo2.maven.org/maven2"}])

(defmulti fetch-project :source)

(defmulti project-url :source)

;; Github
;; ------

(defmethod project-url :github
  [project]
  (format "https://raw.github.com/%s/master/project.clj"
          (:name project)))

(defmethod fetch-project :github
  [project]
  (edn/read
   (PushbackReader.
     (io/reader
       (project-url project)))))

;; Projects
;; --------

(defn project-map [project]
  (apply
    hash-map
    (drop 3 (fetch-project project))))

(defn latest-version [repository [dep-name _]]
  (-> (format "%s/%s/%s/maven-metadata.xml"
              (:url repository)
              dep-name
              dep-name)
      (java.net.URL.)
      (html-resource)
      (select [:release])
      (first)
      :content
      (first)))

(defn dep->latest [dependency]
  (reduce
    (fn [return repository]
      (if (nil? return)
        (latest-version
          repository
          dependency)
        return))
    nil
    repositories))

