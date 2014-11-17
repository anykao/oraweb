(ns oraweb.core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [oraweb.handler :refer [app]]
            )
  (:gen-class))

(defn start-jetty []
  (println "clojars-web: starting jetty on" (str "http://localhost:" 3000))
  (run-jetty #'app {:join? false}))
(defn -main [& args]
  (start-jetty))

