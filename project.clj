(defproject boriskourt-hist "0.2.3"
  :description "Demo for Histograph"
  :url "-"
  
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2277"]
                 [kioo "0.4.1-SNAPSHOT"]
                 [om "0.6.5"]
                 [ankha "0.1.4-SNAPSHOT"]
                 [org.clojure/core.async "0.1.303.0-886421-alpha"]
                 [secretary "1.2.0"]]

  :plugins [[lein-cljsbuild "1.0.3"]
            [lein-ancient "0.6.0-SNAPSHOT"]]

  :source-paths ["src"]
  :resource-paths ["resources"]

  :cljsbuild {:builds {:prod {:source-paths ["src/cljs"]
                              :compiler {:output-to  "resources/js/site.js"
                                         :source-map "resources/js/site.js.map"
                                         :output-dir "resources/js/out"
                                         :optimizations :none
                                         :pretty-print  false
                                         ;;:preamble ["includes/react.js"]
                                         :externs ["includes/react.js"]
                                         :closure-warnings {:externs-validation :off
                                                            :non-standard-jsdoc :off}}}}})
