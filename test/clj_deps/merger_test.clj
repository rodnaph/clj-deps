
(ns clj-deps.merger_test
  (:require [clj-deps.merger :refer [merge-dependencies]]
            [clojure.test :refer :all]))

(def project
  {:dependencies [['bar "1.1.1"]]
   :dev-dependencies [['bazzle "1.1.3"]]
   :plugins [['foo "1.2.3"]]
   :profiles {:dev {:dependencies [['qwerty "1.1.1"]]}
              :foo {:dependencies [['poiliop "1.1.1"]]}}})

(deftest test-all-dependencies-are-merged
  (is (= 5 (count (:all-dependencies (merge-dependencies project))))))

(run-tests)

