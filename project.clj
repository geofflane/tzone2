(defproject tzone "0.0.1-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [io.pedestal/pedestal.service "0.2.2"]
                 [io.pedestal/pedestal.service-tools "0.2.2"]

                 ;; Remove this line and uncomment the next line to
                 ;; use Tomcat instead of Jetty:
                 [io.pedestal/pedestal.jetty "0.2.2"]
                 ;; [io.pedestal/pedestal.tomcat "0.2.2"]
                 [com.datomic/datomic-free "0.9.4497"]
                 [clj-time "0.6.0"]
                 [im.chit/purnam "0.1.8"]
                 [org.clojure/clojurescript "0.0-2156"]]
  :plugins [[lein-cljsbuild "1.0.2"]]
  :min-lein-version "2.0.0"
  :resource-paths ["config", "resources"]
  :source-paths ["src/clj"]
  :test-paths ["test/clj"]
  :aliases {"run-dev" ["trampoline" "run" "-m" "tzone.server/run-dev"]}
  :repl-options  {:init-ns user
                  :init (try
                          (use 'io.pedestal.service-tools.dev)
                          (require 'tzone.service)
                          ;; Nasty trick to get around being unable to reference non-clojure.core symbols in :init
                          (eval '(init tzone.service/service #'tzone.service/routes))
                          (catch Throwable t
                            (println "ERROR: There was a problem loading io.pedestal.service-tools.dev")
                            (clojure.stacktrace/print-stack-trace t)
                            (println)))
                  :welcome (println "Welcome to pedestal-service! Run (tools-help) to see a list of useful functions.")}
  :cljsbuild
    {:builds [{:source-paths ["src/cljs" "test/cljs"],
               :id "unit-test",
               :compiler {:pretty-print true,
                          :output-to "resources/public/js/test.js",
                          :optimizations :whitespace}}
              {:source-paths ["src/cljs"],
               :id "run",
               :compiler {:pretty-print true,
                          :output-to "resources/public/js/main.js",
                          :optimizations :whitespace}}]}
  :main ^{:skip-aot true} tzone.server)
