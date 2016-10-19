(ns espdig-www.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [devtools.core :as devtools]
            [espdig-www.handlers]
            [espdig-www.subs]
            [espdig-www.views :as views]
            [espdig-www.config :as config]
            ;; cljsjs
            cljsjs.filesaverjs))


(defn dev-setup []
  (when config/debug?
    (println "dev mode")
    (devtools/install!)))

(defn mount-root []
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (re-frame/dispatch-sync
   [:initialize-db "data.json"])
  (dev-setup)
  (mount-root))
