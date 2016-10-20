(ns espdig-www.subs
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [re-frame.core :as re-frame]))

(re-frame/register-sub
 :search
 (fn [db]
   (reaction
    (let [term (:app/search @db)
          results (if (clojure.string/blank? term)
                    (group-by :media/author (:media/list @db))
                    (filter #(re-find
                              (re-pattern (str "(?i)" term))
                              (:media/name %)) (:media/list @db)))]
      {:term term
       :results results}))))

(re-frame/register-sub
 :loading?
 (fn [db]
   (reaction (:app/loading? @db))))

(re-frame/register-sub
 :current-action
 (fn [db]
   (reaction {:action (:app/primary-action @db)
              :context (:app/primary-context @db)})))
