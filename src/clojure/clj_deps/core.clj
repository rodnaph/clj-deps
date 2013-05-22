
(ns clj-deps.core
  (:require [clj-deps.web :as web]
            [ring.adapter.jetty :refer [run-jetty]]))

(defn start-web
  ([] (start-web {}))
  ([options]
    (run-jetty
      web/app
      (merge {:port 9001}
             options))))

(defn -main []
  (start-web))

