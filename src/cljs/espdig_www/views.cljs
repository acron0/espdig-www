(ns espdig-www.views
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as re-frame]
            [re-com.core :as re-com]
            [espdig-www.strings :refer [get-string]]
            [espdig-www.widgets :as widget]
            [goog.string :as gstr]))

(defn title []
  (fn []
    [re-com/v-box
     :justify :center
     :align :center
     :children [[:img.logo
                 {:src "/img/logo.png"}]
                [re-com/title
                 :label (get-string :string/subtitle)
                 :level :level4]]]))

(defn search-input [input]
  (fn [input]
    [re-com/h-box
     :align :center
     :width "40%"
     :children [[re-com/box
                 :size "16px"
                 :child [re-com/md-icon-button
                         :md-icon-name "zmdi-search"
                         :size :smaller
                         :style {:pointer-events "none"}
                         :on-click #()]]
                [re-com/gap :size "5px"]
                [re-com/input-text
                 :model input
                 :change-on-blur? false
                 :width "95%"
                 :placeholder (get-string :string/search-placeholder)
                 :on-change #(re-frame/dispatch [:update-search %])]]]))

(defn fix-url
  [url]
  (let [x       (.lastIndexOf url "/")
        frag    (subs url (inc x))
        encoded (gstr/urlEncode frag)]
    (str (subs url 0 x) "/" encoded)))

(defn media-item [item author?]
  (fn [{:keys [media/name
               audio/url
               media/author] :as item}]
    [re-com/h-box
     :class "media-item"
     :justify :between
     :children
     [[re-com/box
       :class "media-item-label-container"
       :size "auto"
       :align :center
       :child (if author?
                [:div.media-item-label [:strong author] name]
                [:div.media-item-label name])]
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
         :on-click #(re-frame/dispatch [:play-video (:media/id item)])]
        [:a
         {:href (fix-url url)}
         [re-com/md-icon-button
          :md-icon-name "zmdi-download"
          :size :smaller
          :on-click #()]]]]]]))

(defn media-items [items author?]
  (fn [items]
    [re-com/v-box
     :class "media-items"
     :children (for [item (->> items
                               (sort-by :media/published-at)
                               (reverse))]
                 [media-item item author?])]))

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
                     :class "search-results"
                     :width "90%"
                     :children
                     [[re-com/gap :size "20px"]
                      (if (not-empty results)
                        [media-items results true]
                        [re-com/box
                         :align :center
                         :child
                         [re-com/label
                          :label (get-string :string/no-matches)]])]]
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
                          [media-items (->> media
                                            (second)
                                            (sort-by :media/published-at)
                                            (reverse)
                                            (take 10)) false]]]])])]])))


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
                      (when (:action @action)
                        [re-com/box
                         :class "widget-container"
                         :child
                         (case (:action @action)
                           :play-video [widget/play-video (:context @action)]
                           [:div "Unknown action"])])
                      [media-display @search]]]]]))))
