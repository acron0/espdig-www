(ns espdig-www.widgets
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as re-frame]
            [re-com.core :as re-com]
            [espdig-www.strings :refer [get-string]]))

(defn download
  []
  (let [action (re-frame/subscribe [:download-action])]
    (fn []
      (let [{:keys [name progress]} @action]
        [re-com/v-box
         :class "download-widget"
         :children
         [[:span.media-item-label
           (str "Downloading: " name)]
          [:div.dw-progress-container
           [re-com/progress-bar
            :model (int (* 100 progress))
            :width "auto"
            :striped? (not= 1 progress)]
           [re-com/md-icon-button
            :md-icon-name "zmdi-download"
            :disabled? (not= 1 progress)]]]]))))
