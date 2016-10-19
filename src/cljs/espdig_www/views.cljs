(ns espdig-www.views
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as re-frame]
            [re-com.core :as re-com]
            [espdig-www.strings :refer [get-string]]
            [espdig-www.widgets :as widget]))

(defn title []
  (fn []
    [re-com/title
     :label (get-string :string/title)
     :level :level1]))

(defn search-input [input]
  (fn [input]
    [re-com/h-box
     :align :center
     :width "40%"
     :children [[re-com/md-icon-button
                 :md-icon-name "zmdi-search"
                 :size :smaller
                 :style {:pointer-events "none"}
                 :on-click #()]
                [re-com/gap :size "5px"]
                [re-com/input-text
                 :model input
                 :change-on-blur? false
                 :width "95%"
                 :placeholder (get-string :string/search-placeholder)
                 :on-change #(re-frame/dispatch [:update-search %])]]]))

(defn media-item [item]
  (fn [{:keys [media/name
               audio/url
               media/author] :as item}]
    (println (keys item))
    [re-com/h-box
     :class "media-item"
     :justify :between
     :children
     [[re-com/box
       :class "media-item-label-container"
       :size "auto"
       :align :center
       :child [:div.media-item-label name]]
      [re-com/h-box
       :align :center
       :children
       [#_[re-com/md-icon-button
           :md-icon-name "zmdi-headset"
           :size :smaller
           :on-click #()]
        [re-com/md-icon-button
         :md-icon-name "zmdi-play"
         :size :smaller
         :on-click #(re-frame/dispatch [:play-video (:video/url item)])]
        [:a
         {:href url}
         [re-com/md-icon-button
          :md-icon-name "zmdi-download"
          :size :smaller
          :on-click #()]]]]]]))

(defn media-items [items]
  (fn [items]
    [re-com/v-box
     :class "media-items"
     :children (for [item (reverse (sort-by :media/published-at items))]
                 [media-item item])]))

(defn media-display [{:keys [term results]}]
  (fn [{:keys [term results]}]
    (let [searching? (not (clojure.string/blank? term))
          label (if searching?
                  :string/search-results
                  :string/recent-media)]
      [re-com/v-box
       :align :center
       :height "100%"
       :width "100%"
       :children [[re-com/title
                   :label (get-string label)
                   :level :level2]
                  (if searching?
                    [re-com/v-box
                     :width "50%"
                     :children
                     [[re-com/gap :size "39px"]
                      [media-items results]]]
                    ;;;
                    [:div.pure-g
                     {:style {:width "100%"
                              :padding "10px"}}
                     (for [media results]
                       ^{:key (first media)}
                       [:div.pure-u-1.pure-u-md-1-2.pure-u-lg-1-3
                        {:style {:padding "10px"}}
                        [re-com/v-box
                         :width "100%"
                         :align :center
                         :children
                         [[re-com/title
                           :label (first media)
                           :level :level3]
                          [media-items (second media)]]]])])]])))


(defn main-panel []
  (let [loading? (re-frame/subscribe [:loading?])
        search (re-frame/subscribe [:search])
        action (re-frame/subscribe [:current-action])]
    (fn []
      (if @loading?
        [re-com/v-box
         :justify :center
         :align :center
         :height "100%"
         :children [[re-com/gap
                     :size "100px"]
                    [re-com/throbber
                     :size :large]]]
        [re-com/h-box
         :justify :center
         :children
         [[re-com/v-box
           :align :center
           :height "100%"
           :width "100%"
           :children [[title]
                      [search-input (:term @search)]
                      [re-com/gap :size "10px"]
                      (when @action
                        [re-com/box
                         :class "widget-container"
                         :child
                         (case @action
                           :downloading [widget/download]
                           [:div])])
                      [media-display @search]]]]]))))
