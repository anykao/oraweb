(defproject oraweb "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :resource-paths ["libs/ojdbc7.jar" "pgweb/static"]
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [org.clojure/java.jdbc "0.3.5"]
                 [com.mchange/c3p0 "0.9.2.1"]
                 [com.taoensso/timbre "3.3.1"]
                 [liberator "0.12.2"]
                 [compojure "1.2.0"]
                 [ring/ring-defaults "0.1.2"]
                 [ring/ring-jetty-adapter "1.3.1"]
                 ;[enlive "1.1.5"]
                 [om "0.7.1"]
                 [environ "0.5.0"]
                 ]
  :ring {:handler oraweb.handler/app}
  :plugins [[lein-ring "0.8.13"]]
  :main ^:skip-aot oraweb.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
