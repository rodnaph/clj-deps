
(ns clj-deps.project-test
  (:require [clj-deps.project :refer :all]
            [clojure.test :refer :all]))

(defn project-edn [_]
  '(defproject foo "1.2.3"
     :dependencies [[bar "1.1"]
                    [baz "1.4"]]))

(def project {:source :github
              :name "fozzle/bazzle"})

(deftest test-project-details-extracted
    (let [desc (with-redefs
                 [clj-deps.project/project->edn project-edn]
                   (description->project project))]
      (is (= "foo" (:name desc)))
      (is (= "1.2.3" (:version desc)))
      (is (= 2 (count (:dependencies desc))))))

;(run-tests)

