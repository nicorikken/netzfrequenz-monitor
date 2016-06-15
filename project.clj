(defproject webapp "0.1.0-SNAPSHOT"
  :description "A basic webapp"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :profiles {:dev {:plugins [[com.jakemccrary/lein-test-refresh "0.15.0"]
                             [lein-ring "0.9.7"]]
                   :dependencies [[org.clojure/tools.namespace "0.2.11"]]
                   :src ["dev-dependencies"]}}
  :dependencies [[compojure "1.5.0"]
                 [hiccup "1.0.5"]
                 [org.clojure/clojure "1.8.0"]
                 [ring/ring-defaults "0.2.0"]
                 [proto-repl "0.1.2"]]
  :ring {:handler webapp.core/handler-with-middleware})
