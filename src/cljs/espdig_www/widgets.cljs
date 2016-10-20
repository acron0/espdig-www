(ns espdig-www.widgets
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as re-frame]
            [re-com.core :as re-com]
            [espdig-www.strings :refer [get-string]]))

#_(defn download
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

(defn get-iframe-dims
  []
  (let [aspect (/ 280 500)
        mult   0.7
        w      (.. js/document -body -offsetWidth)
        w'     (* w mult)
        h'     (* (* w aspect) mult)]
    [w' h']))

(defn play-video
  [url]
  (let [[w h] (get-iframe-dims)]
    [re-com/v-box
     :class "play-video-widget"
     :children
     [[:iframe#video-frame
       {:type "text/html"
        :src (str "http://www.youtube.com/embed/" url)
        :width (str w "px")
        :height (str h "px")
        :scrolling "no"
        :frame-border "0"
        :allow-transparency "true"
        :allow-full-screen "true"}]]]))

(.addEventListener
 js/window "resize"
 #(when-let [frame (.getElementById js/document "video-frame")]
    (let [[w h] (get-iframe-dims)]
      (aset frame "height" h)
      (aset frame "width"  w))))
