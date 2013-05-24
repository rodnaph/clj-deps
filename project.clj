
(defproject clj-deps "0.1.0-SNAPSHOT"
  :description "Clojure dependency checker"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/core.cache "0.6.3"]
                 [boxuk/versions "0.6.1"]
                 [compojure "1.1.5"]
                 [ring/ring-jetty-adapter "1.1.8"]
                 [ring/ring-devel "1.1.8"]
                 [enlive "1.1.1"]]
  :source-paths ["dev" "src/clojure" "src/html"]
  :main clj-deps.core)

