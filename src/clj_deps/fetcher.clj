
(ns clj-deps.fetcher
  (:require [net.cgrand.enlive-html :refer :all]
            [clojure.string :as s]))

(def ^{:private true}
  repositories
  "Standard repositories to check.  Extra repos per-project can be added."
  [{:name "Maven Central"
    :url "http://repo2.maven.org/maven2"}
   {:name "Clojars"
    :url "http://clojars.org/repo"}])

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
  [repository dependency]
  (let [metadata (metadata-resource repository dependency)]
    (->> (select metadata [:version])
         (map (comp first :content)))))

;; Public
;; ------

(defn dep->versions
  ([dependency] (dep->versions [] dependency))
  ([extra-repositories dependency]
    (reduce
      (fn [_ repository]
        (if-let [versions (versions-for repository dependency)]
          (reduced versions)))
      (concat
        repositories
        extra-repositories))))

