
(ns clj-deps.maven
  (:require [clj-deps.cache :refer [with-cache]]
            [net.cgrand.enlive-html :refer :all]
            [clojure.string :as s]))

(def ^{:private true}
  repositories
  "Standard repositories to check.  Extra repos per-project can be added."
  [{:name "Maven Central"
    :url "http://repo2.maven.org/maven2"}
   {:name "Clojars"
    :url "http://clojars.org/repo"}])

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
  (try
    (-> (metadata-url repository dependency)
      (java.net.URL.)
      (html-resource))
    (catch Exception e {})))

(defn- versions-for
  "Returns the versions for a dependency in a repository."
  [repository dependency]
  (let [cache-id (format "dep::%s-%s"
                         (:url repository)
                         (str (first dependency)))]
    (with-cache cache-id
      (let [metadata (metadata-resource repository dependency)]
        (->> (select metadata [:version])
             (map (comp first :content)))))))

(defn- dep->versions
  "Resolve a dependencies available versions."
  ([dependency] (dep->versions [] dependency))
  ([extra-repositories dependency]
    (reduce
      (fn [_ repository]
        (if-let [versions (versions-for repository dependency)]
          (reduced versions)))
      (concat
        repositories
        extra-repositories))))

(defn- name-and-current
  "Extract just name and current version for dependency"
  [[dep-name current]]
  [dep-name current])

;; Public
;; ------

(defn project->versions
  "Adds the latest version to the dependency vector.
  [foo '1.2.3'] => [foo '1.2.3' ['1.2.4' '1.2.3' '1.2.2']]"
  [project]
  (assoc
    project
    :dependencies
    (map #(conj (name-and-current %) (dep->versions %))
         (:dependencies project))))

