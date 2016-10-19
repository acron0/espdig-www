(defproject espdig-www "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.8.51"]
                 [reagent "0.5.1"]
                 [binaryage/devtools "0.6.1"]
                 [re-frame "0.7.0"]
                 [re-com "0.8.3"]
                 [garden "1.3.2"]
                 [cljs-http "0.1.41"]
                 [cljsjs/filesaverjs "1.1.20151003-0"]]

  :plugins [[lein-cljsbuild "1.1.3"]
            [lein-garden "0.2.6"]]

  :min-lein-version "2.5.3"

  :source-paths ["src/clj"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"
                                    "test/js"
                                    "resources/public/css"]

  :figwheel {:css-dirs ["resources/public/css"]}

  :garden {:builds [{:id           "screen"
                     :source-paths ["src/clj"]
                     :stylesheet   espdig-www.css/screen
                     :compiler     {:output-to     "resources/public/css/screen.css"
                                    :pretty-print? true}}]}

  :profiles
  {:dev
   {:source-paths ["dev-src"]
    :dependencies [[com.cemerick/piggieback "0.2.1"]
                   [org.clojure/tools.nrepl "0.2.12"]
                   [figwheel "0.5.4-3"]
                   [figwheel-sidecar "0.5.4-3"]]

    :plugins      [[lein-figwheel "0.5.4-3"]
                   [lein-doo "0.1.6"]]}
   :data {:source-paths ["data-src"]
          :dependencies [[amazonica "0.3.73"]]}}

  :cljsbuild
  {:builds
   [{:id           "dev"
     :source-paths ["src/cljs"]
     :figwheel     {:on-jsload "espdig-www.core/mount-root"}
     :compiler     {:main                 espdig-www.core
                    :output-to            "resources/public/js/compiled/app.js"
                    :output-dir           "resources/public/js/compiled/out"
                    :asset-path           "js/compiled/out"
                    :source-map-timestamp true}}

    {:id           "min"
     :source-paths ["src/cljs"]
     :compiler     {:main            espdig-www.core
                    :output-to       "resources/public/js/compiled/app.js"
                    :optimizations   :advanced
                    :closure-defines {goog.DEBUG false}
                    :pretty-print    false}}
    {:id           "test"
     :source-paths ["src/cljs" "test/cljs"]
     :compiler     {:output-to     "resources/public/js/compiled/test.js"
                    :main          espdig-www.runner
                    :optimizations :none}}]}
  :aliases {"upload-data" ["with-profile" "data" "run" "-m" "espdig-www.upload-data"]})
