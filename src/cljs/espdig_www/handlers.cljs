(ns espdig-www.handlers
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [re-frame.core :as re-frame]
            [espdig-www.db :as db]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<! chan]]))


(defn request-data
  [uri]
  (go (let [r (<! (http/get uri {:with-credentials? false}))]
        (if (= (:status r) 200)
          (re-frame/dispatch [:data-received (:body r)])
          (re-frame/dispatch [:data-failed r])))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(re-frame/register-handler
 :data-received
 (fn  [app-state [_ data]]
   (assoc app-state
          :media/list (:media/list data)
          :app/loading? false)))

(re-frame/register-handler
 :data-failed
 (fn  [app-state _]
   (assoc app-state
          :app/error :string/data-error
          :app/loading? false)))

(re-frame/register-handler
 :initialize-db
 (fn  [_ [_ data-uri]]
   (request-data data-uri)
   db/default-db))

(re-frame/register-handler
 :update-search
 (fn  [app-state [_ value]]
   (assoc app-state :app/search value)))
