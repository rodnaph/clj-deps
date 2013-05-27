
(ns clj-deps.core
  (:require [clj-deps.web :as web]
            [confo.core :refer [confo]]
            [clj-logging-config.log4j :refer [set-logger!]]
            [ring.adapter.jetty :refer [run-jetty]])
  (:gen-class))

(def config (confo :cljdeps
                   :port 9001
                   :loglevel :debug
                   :logpattern "%n %m"
                   :logfile "logs/access.log"))

(defn configure-logging []
  (set-logger! "clj-deps"
               :level (:loglevel config)
               :out (org.apache.log4j.RollingFileAppender.
                      (org.apache.log4j.EnhancedPatternLayout.
                        (:logpattern config))
                      (:logfile config)
                      true)))

(defn start-web
  [options app]
  (run-jetty
    app
    (merge (select-keys config [:port])
           options)))

(defn -main [& [port]]
  (configure-logging)
  (start-web {:port (Integer/parseInt port)}
             web/prod-app))

