
(ns clj-deps.maven
  (:require [clj-deps.log :refer :all]
            [clj-deps.cache :refer [with-cache]]
            [net.cgrand.enlive-html :refer :all]
            [clojure.string :as s]))

(def ^{:private true}
  repositories
  "Standard repositories to check.  Extra repos per-project can be added."
  [{:name "Maven Central"
    :url "http://repo2.maven.org/maven2"}
   {:name "Clojars"
    :url "http://clojars.org/repo"}])

(defn- load-resource [url]
  (-> url
      (java.net.URL.)
      (html-resource)))

(defn- dependency-parts
  "Split a dependency name into its org/artifact parts"
  [[artifact]]
  (if-let [parts (re-matches #"(.+?)\/(.*)" (str artifact))]
    (drop 1 parts)
    (map str [artifact artifact])))

(defn- metadata-url
  "Return the URL for a dependencies metadata in a repository"
  [repository dependency]
  (let [[dep-org dep-artifact] (dependency-parts dependency)]
    (format "%s/%s/%s/maven-metadata.xml"
            (:url repository)
            (s/replace dep-org "." "/")
            dep-artifact)))

(defn- metadata-resource
  "Return an EnLive resource for the dependencies metadata in the repository."
  [repository dependency]
  (let [url (metadata-url repository dependency)
        evt {:type "url.fetch"
             :url url}]
    (try
      (do
        (info evt)
        (load-resource url))
      (catch Exception e
        (error evt)
        nil))))

(defn- versions-for
  "Returns the versions for a dependency in a repository."
  [repository dependency]
  (if-let [metadata (metadata-resource repository dependency)]
    (->> (select metadata [:version])
         (map (comp first :content)))))

(defn- dep->versions
  "Resolve a dependencies available versions."
  ([dependency] (dep->versions [] dependency))
  ([extra-repositories dependency]
    (let [all-repositories (apply vector
                                  (concat repositories extra-repositories))]
      (with-cache (str (first dependency))
        (reduce
          (fn [_ repository]
            (if-let [versions (versions-for repository dependency)]
              (reduced versions)))
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

