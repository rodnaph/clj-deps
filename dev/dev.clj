
(ns dev
  (:require [clj-deps.core :refer :all]
            [clj-deps.web :as web]
            [ring.middleware.stacktrace :refer [wrap-stacktrace]]))

(defonce server (atom nil))

(configure-logging)

(defn start []
  (reset!
    server
    (start-web {:join? false}
               web/dev-app)))

(defn stop []
  (.stop @server))

