(ns espdig-www.css
  (:require [garden.def :refer [defstyles]]
            [garden.units :refer [px percent em]]
            [garden.color :refer [rgba]]
            [garden.core :refer [css]]))

(def media-border
  [[(px 1) :solid 'silver]])

(defstyles screen
  [:html {:overflow-y :scroll}]

  [:.level3 {:font-size (em 1.2)}]

  [:.media-items
   {:border-bottom media-border
    :width (percent 100)}]

  [:.media-item
   {:border media-border
    :border-bottom (px 0)
    :height (px 24)
    :cursor :pointer}
   [:&:hover
    {:background-color 'greenyellow}]]

  [:.media-item-label-container
   {:display :flex
    :margin-left (px 10)}]

  [:.media-item-label
   {:white-space :nowrap
    :overflow :hidden
    :text-overflow :ellipsis
    :min-width (px 0)}]

  [:.widget-container
   {:min-height (px 40)
    :margin-top (px 15)
    :width (percent 30)}]

  [:.download-widget
   {:width (percent 100)}
   [:.dw-progress-container
    {:position "relative"}
    [:.rc-progress-bar
     {:position :absolute
      :left (px 0)
      :right (px 32)}]
    [:.rc-md-icon-button
     {:position "absolute"
      :right "0"}]]])
