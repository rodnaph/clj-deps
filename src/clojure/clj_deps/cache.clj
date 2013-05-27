
(ns clj-deps.cache
  (:require [clj-deps.log :refer :all]
            [clj-deps.util :refer [FIVE_MINUTES_IN_MILLIS]]
            [clojure.core.cache :refer :all]))

(def cache-store
  (atom (ttl-cache-factory {} :ttl FIVE_MINUTES_IN_MILLIS)))

;; Public
;; ------

(defmacro with-cache [id & body]
  `(let [id# (keyword ~id)]
     (if (has? @cache-store id#)
       (do
         (info {:type "cache.hit"
                :id ~id})
         (id# (hit @cache-store id#)))
       (let [data# (do ~@body)]
         (info {:type "cache.miss"
                :id ~id})
         (reset!
           cache-store
           (miss @cache-store id# data#))
         data#))))

