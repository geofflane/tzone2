(ns tzone.core
  (:require [ring.util.response :as ring-response]
            [io.pedestal.service.http :as bootstrap]
            [clj-time.core :as t]
            [clj-time.format :as tf]
            [clj-time.local :as tl]))


(def format-str "yyyy-MM-dd'T'HH:mm:ss.SSSS")

(defn now-in-tz
  [tz]
  (t/to-time-zone (tl/local-now) tz))

(defn time-str
  [t tz]
  (tf/unparse (tf/formatter format-str tz) t))

(defn str-time
  [t tz]
  (tf/parse (tf/formatter format-str tz) t))

(defn tzones [request]
  (ring-response/response (map (fn [v] {"tzone" v}) (org.joda.time.DateTimeZone/getAvailableIDs))))

(defn current-time
  [{{:keys [to]} :params}]
  (let [tz (t/time-zone-for-id to)
        new-time (now-in-tz tz)]
    (ring-response/response {:tz to :time (time-str new-time tz)})))

(defn convert-time
  [{{:keys [to from time]} :params}]
  (let [totz (t/time-zone-for-id to)
        fromtz (t/time-zone-for-id from)
        new-time (str-time time fromtz)]
    (ring-response/response {:tz to :time (time-str new-time totz)})))
