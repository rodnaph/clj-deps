
(ns clj-deps.merger_test
  (:require [clj-deps.merger :refer [merge-dependencies]]
            [clojure.test :refer :all]))

(def project
  {:dependencies [['bar "1.1.1"] ['bar2 "1.1.1"]]
   :dev-dependencies [['bazzle "1.1.3"] ['bazzle2 "2.2.2"]]
   :plugins [['foo "1.2.3"] ['foo2 "2.3.4"]]
   :profiles {:dev {:dependencies [['qwerty "1.1.1"] ['qwerty2 "3.3"]]}
              :foo {:dependencies [['poiliop "1.1.1"] ['poiliop2 "5.4.3"]]}}})

(deftest test-all-dependencies-are-merged
  (is (= 10 (count (:all-dependencies (merge-dependencies project))))))

;(run-tests)

