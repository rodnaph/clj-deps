
(ns clj-deps.maven-test
  (:require [clj-deps.maven :refer :all]
            [clojure.test :refer :all]
            [net.cgrand.enlive-html :refer [html-resource]]))

(def project
  {:dependencies '["foo" "1.2.3"]})

(defn project-resource [& args]
  (html-resource
    (java.io.File.
      "test/clj_deps/metadata.xml")))

(defn get-versions []
  (with-redefs
    [clj-deps.maven/load-resource project-resource]
      (project->versions project)))

(deftest test-versions-extracted-for-project
  (let [[_ _ versions] (-> (get-versions)
                           :dependencies
                           first)]
    (is (= 54 (count versions)))))

(run-tests)

