
(ns clj-deps.fetcher
  (:require [net.cgrand.enlive-html :refer :all]))

(def ^{:private true}
  repositories
  "Standard repositories to check.  Extra repos per-project can be added."
  [{:name "Clojars"
    :url "http://clojars.org/repo"}
   {:name "Maven Central"
    :url "http://repo2.maven.org/maven2"}])

(defn dependency-parts
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
            dep-org
            dep-artifact)))

(defn- metadata-resource
  "Return an EnLive resource for the dependencies metadata in the repository."
  [repository dependency]
  (try
    (-> (metadata-url repository dependency)
      (java.net.URL.)
      (html-resource))
    (catch Exception e {})))

(def ^{:private true}
  content-for-node
  "Given a node with a list of content, get the first item in it."
  (comp first :content first))

(defn- version-node
  "Select the latest version from the Maven metadata"
  [metadata]
  (let [release (select metadata [:release])]
    (if (empty? release)
      (select metadata [:version])
      release)))

(defn- latest-version
  "Fetch the latest version of a library from a repository, or return nil
  if it's not found in that repository."
  [repository dependency]
  (-> (metadata-resource repository dependency)
      (version-node)
      (content-for-node)))

;; Public
;; ------

(defn dep->latest
  "Resolve a dependency (eg. [foo '1.2.3']) to its latest version."
  ([dependency] (dep->latest [] dependency))
  ([extra-repositories dependency]
    (reduce
      (fn [return repository]
        (if (nil? return)
          (latest-version
            repository
            dependency)
          return))
      nil
      (concat
        repositories
        extra-repositories))))

