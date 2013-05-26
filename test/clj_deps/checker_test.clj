
(ns clj-deps.checker-test
  (:require [clj-deps.checker :refer :all]
            [clojure.test :refer :all]))

(def stable-project
  {:dependencies
   [['foo "1.2.3" ["1.2.3"]]]})

(def unstable-project
  {:dependencies
   [['foo "1.2.3" ["1.2.3" "1.2.3-SNAPSHOT"]]]})

(def outdated-project
  {:dependencies
   [['foo "1.2.3" ["1.2.4"]]]})

(deftest stable-and-unstable-deps-detectd
  (let [status (project->status stable-project)]
    (is (empty? (:stable status)))
    (is (empty? (:unstable status))))
  (let [status (project->status unstable-project)]
    (is (empty? (:stable status)))
    (is (not (empty? (:unstable status)))))
  (let [status (project->status outdated-project)]
    (is (not (empty? (:stable status))))
    (is (not (empty? (:unstable status))))))

;(run-tests)

