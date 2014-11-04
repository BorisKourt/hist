(defproject boriskourt-hist "0.6"
  :description "Demo for Histograph"
  :url "-"
  
  :dependencies [[org.clojure/clojure "1.7.0-alpha3"]
                 [org.clojure/clojurescript "0.0-2371"]
                 [kioo "0.4.1-SNAPSHOT"]
                 [om "0.7.3"]
                 [ankha "0.1.4"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [secretary "1.2.1"]
                 [org.clojure/core.typed "0.2.72"]]

  :plugins [[lein-cljsbuild "1.0.3"]
            [lein-ancient "0.6.0-SNAPSHOT"]
            [lein-gorilla "0.3.2" :exclusions [org.clojure/clojure]]]

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
