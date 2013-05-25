
(ns clj-deps.log
  (:require [clojure.tools.logging :as l]))

(defn pr-evt [evt]
  (merge {:ts (System/currentTimeMillis)} evt))

;; Public
;; ------

(defn info [evt]
  (l/info (pr-evt evt)))

(defn debug [evt]
  (l/debug (pr-evt evt)))

(defn error [evt]
  (l/error (pr-evt evt)))

