(ns espdig-www.upload-data
  (:require [clojure.string :as str]
            [amazonica.aws.s3 :as aws]
            [amazonica.aws.s3transfer :as aws-s3]
            [clojure.java.io :as io])
  (:import java.util.zip.GZIPOutputStream)
  (:gen-class))

  (def region "eu-west-1")
(def dir "resources/public")
(def blacklist #{"data.json"})
(def profile "espdig")
(def bucket "www.espop.net")

(defn -main
  "Uploads the data to S3"
  []
  (println "Uploading to...\nbucket:" bucket "\nregion:" region "\ndir:" dir)
  (time
   (let [files (file-seq (clojure.java.io/file dir))
         upload-fn (fn [f]
                     (when-not (.isDirectory f)
                       (let [fname (subs (str/replace (str f) dir "") 1)
                             fobj  (io/file (str f))]
                         (when-not (contains? blacklist fname)
                           (println "-" fname)
                           (aws/put-object {:profile profile
                                            :endpoint   region}
                                           :bucket-name bucket
                                           :key fname
                                           :file fobj)))))]
     (run! upload-fn files))))
