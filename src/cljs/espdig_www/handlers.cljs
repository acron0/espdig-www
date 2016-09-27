(ns espdig-www.handlers
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [re-frame.core :as re-frame]
            [espdig-www.db :as db]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<! chan]]))


(defn request-data
  [uri]
  (go (let [r (<! (http/get uri))]
        (if (= (:status r) 200)
          (re-frame/dispatch [:data-received (:body r)])
          (re-frame/dispatch [:data-failed r])))))

(defn download-file
  [uri name]
  (let [uri "https://s3-eu-west-1.amazonaws.com/espdig-m4a/foo.png"]
    (go (let [r (<! (http/head uri))]
          (if (= (:status r) 200)
            (let [progress-channel (chan)
                  result-channel (http/get uri {:progress progress-channel})]
              (loop []
                (let [{:keys [total loaded]} (<! progress-channel)]
                  (re-frame/dispatch [:download-file-progress (/ loaded total)])
                  (if-not (= total loaded)
                    (recur)
                    (do
                      (println "Download completed" total)
                      (re-frame/dispatch [:download-file-received (<! result-channel) name]))))))
            (re-frame/dispatch [:download-file-failed r]))))))

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

;;
(re-frame/register-handler
 :download-audio
 (fn  [app-state [_ uri name]]
   (download-file uri name)
   (assoc app-state
          :app/primary-action :downloading
          :download/context {:name name
                             :progress 0
                             :data nil})))

;;
(re-frame/register-handler
 :download-file-progress
 (fn  [app-state [_ percent]]
   (assoc-in app-state [:download/context :progress] percent)))

(defn string-to-bytes
  "http://stackoverflow.com/a/1242596"
  [s]
  (let [convert
        (fn [i]
          (loop [ch (.charCodeAt s i)
                 st []]
            (let [st' (conj st (bit-and ch 0xFF))
                  ch' (bit-shift-right ch 8)]
              (if (zero? ch')
                (let [r (reverse st')]
                  r)
                (recur ch' st')))))
        result (->> (count s)
                    (range)
                    (mapcat convert))]
    result))

(defn string-to-bytes2
  "http://stackoverflow.com/a/4528265"
  [s]
  (let [convert (fn [i]
                  (let [ch (.charCodeAt s i)]
                    (if (< ch 0x7F)
                      (do
                        (println i "Returning" ch)
                        [ch])
                      (let [h (-> (js/encodeURIComponent (.charAt s i))
                                  (.substr 1)
                                  (.split \%))
                            r (mapv #(js/parseInt (nth h %) 16)
                                    (range (count h)))]
                        (println i "Returning" r)
                        r))))
        result (->> (count s)
                    (range)
                    (mapcat convert))]
    result))

(re-frame/register-handler
 :download-file-received
 (fn  [app-state [_ {:keys [body headers] :as h} name]]
   (let [bytes (string-to-bytes2 body)
         _ (println (take 4 bytes))
         u8 (js/Uint8Array. bytes)
         bl (js/Blob. #js [u8] #js {:type (get headers "content-type")})]
     (js/saveAs bl (str "foo" ".png"))
     (assoc-in app-state [:download/context :data] bl))))

(re-frame/register-handler
 :download-file-failed
 (fn  [app-state [_ data]]
   (println "DOWNLOADING FAILED")
   app-state))
