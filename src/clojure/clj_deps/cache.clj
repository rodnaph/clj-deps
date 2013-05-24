
(ns clj-deps.cache
  (:require [clojure.core.cache :refer :all]))

(def FIVE_MINUTES (* 1000 60 5))

(def cache-store
  (atom (ttl-cache-factory {} :ttl FIVE_MINUTES)))

;; Public
;; ------

(defmacro with-cache [id & body]
  `(let [id# (keyword ~id)]
     (if (has? @cache-store id#)
       (do
         (println "cache hit:" ~id)
         (id# (hit @cache-store id#)))
       (let [data# (do ~@body)]
         (println "cache miss:" ~id)
         (reset!
           cache-store
           (miss @cache-store id# data#))
         data#))))

