
(ns clj-deps.maven
  (:require [clj-deps.log :refer :all]
            [clj-deps.cache :refer [with-cache]]
            [net.cgrand.enlive-html :refer :all]
            [clojure.string :as s])
  (:import (java.net URL)))

(def ^{:private true}
  repositories
  "Standard repositories to check.  Extra repos per-project can be added."
  [{:name "Maven Central"
    :url "http://repo2.maven.org/maven2"}
   {:name "Clojars"
    :url "http://clojars.org/repo"}])

(defn- load-resource [url]
  (html-resource (URL. url)))

(defn- dependency-parts
  "Split a dependency name into its org/artifact parts"
  [[artifact]]
  (if-let [parts (re-matches #"(.+?)\/(.*)"
                             (str artifact))]
    (drop 1 parts)
    (repeat 2 (str artifact))))

(defn- metadata-url
  "Return the URL for a dependencies metadata in a repository"
  [repository dependency]
  (let [[org artifact] (dependency-parts dependency)]
    (format "%s/%s/%s/maven-metadata.xml"
            (:url repository)
            (s/replace org "." "/")
            artifact)))

(defn- resource
  "Return an EnLive resource for the dependencies metadata in the repository."
  [repository dependency]
  (let [url (metadata-url repository dependency)
        evt {:type "url.fetch" :url url}]
    (info evt)
    (try
      (load-resource url)
      (catch Exception e
        (error evt)))))

(defn- versions-for
  "Returns the versions for a dependency in a repository."
  [repository dependency]
  (if-let [metadata (resource repository dependency)]
    (map (comp first :content)
         (select metadata [:version]))))

(defn- dep->versions
  "Resolve a dependencies available versions."
  ([dependency] (dep->versions [] dependency))
  ([extra-repositories dependency]
    (let [all-repositories (apply vector
                                  (concat repositories extra-repositories))]
      (with-cache (-> dependency first str)
        (reduce
          #(if-let [versions (versions-for %2 dependency)]
             (reduced versions))
          nil
          all-repositories)))))

(def take-vec
  "Take n from coll and return as vector"
  (comp (partial apply vector) take))

;; Public
;; ------

(defn project->versions
  "Adds the latest version to the dependency vector.
  [foo '1.2.3'] => [foo '1.2.3' ['1.2.4' '1.2.3' '1.2.2']]"
  [project]
  (assoc
    project
    :dependencies
    (doall
      (map #(conj (take-vec 2 %)
                  (dep->versions %))
           (:dependencies project)))))

