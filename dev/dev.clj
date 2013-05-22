
(ns dev
  (:require [clj-deps.core :refer :all]))

(defonce server (atom nil))

(defn start []
  (reset!
    server
    (start-web {:join? false})))

(defn stop []
  (.stop @server))

