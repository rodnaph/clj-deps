
(ns clj-deps.cache
  (:require [clj-deps.log :refer :all]
            [clj-deps.util :refer [FIVE_MINUTES_IN_MILLIS]]
            [clojure.core.cache :refer :all]))

(def cache-store
  (atom (ttl-cache-factory {} :ttl FIVE_MINUTES_IN_MILLIS)))

(defn cache-hit [id]
  (info {:type "cache.hit"
         :id (name id)})
  (id (hit @cache-store id)))

(defn cache-miss [id data]
  (info {:type "cache.miss"
         :id (name id)})
  (reset!
    cache-store
    (miss @cache-store id data))
  data)

;; Public
;; ------

(defmacro with-cache [id & body]
  `(let [id# (keyword ~id)]
     (if (has? @cache-store id#)
       (cache-hit id#)
       (cache-miss id# (do ~@body)))))

