(ns tzone.service
    (:require [io.pedestal.service.http :as bootstrap]
              [io.pedestal.service.http.route :as route]
              [io.pedestal.service.http.body-params :as body-params]
              [io.pedestal.service.http.route.definition :refer [defroutes]]
              [io.pedestal.service.interceptor :refer [defbefore]]
              [io.pedestal.service.impl.interceptor :refer [terminate]]
              [ring.util.response :as ring-resp]
              [clj-time.core :as t]
              [clj-time.format :as tf]
              [clj-time.local :as tl]
              [cheshire.core :refer :all]
              [cheshire.generate :refer [add-encoder]]))

(defn home-page
  [request]
  (ring-resp/redirect "index.html"))

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

(defn tzones [reques]
  (bootstrap/json-response (map (fn [v] {"tzone" v}) (org.joda.time.DateTimeZone/getAvailableIDs))))

(defn current-time
  [{{:keys [to]} :params}]
  (let [tz (t/time-zone-for-id to)
        new-time (now-in-tz tz)]
    (bootstrap/json-response {:tz to :time (time-str new-time tz)})))

(defn convert-time
  [{{:keys [to from time]} :params}]
  (let [totz (t/time-zone-for-id to)
        fromtz (t/time-zone-for-id from)
        new-time (str-time time fromtz)]
    (bootstrap/json-response {:tz to :time (time-str new-time totz)})))

(defn valid [api-key] (= "xxx" api-key))
(defn stop-and-respond [context value]
  (assoc context :response value))

;; (defn- api-key-for [context] (-> context :request :params :key))
(defn- api-key-for [context] (get-in context [:request :params :key]))

(defbefore with-api-key [context]
  "Security interceptor"
  (let [api-key (api-key-for context)]
    (cond
      (nil? api-key) (stop-and-respond context (bootstrap/json-response {:error "Must pass API key"}))
      (not (valid api-key)) (stop-and-respond context (bootstrap/json-response {:error "Unknown API key"}))
      :otherwise context)))

(defbefore record-usage [context]
  (let [api-key (api-key-for context)]
    (println "ApiKey used: " api-key)
    context))

(defroutes routes
  [[["/" {:get home-page}
     ;; Set default interceptors for /about and any other paths under /
     ^:interceptors [(body-params/body-params) bootstrap/html-body]
     ["/tzone" {:get tzones}]
     ["/convertCurrent"
      ^:interceptors [with-api-key record-usage]
      {:get current-time}]
     ["/convertTime"
      ^:interceptors [with-api-key record-usage]
      {:get convert-time}]]]])

;; Consumed by tzone.server/create-server
;; See bootstrap/default-interceptors for additional options you can configure
(def service {:env :prod
              ;; You can bring your own non-default interceptors. Make
              ;; sure you include routing and set it up right for
              ;; dev-mode. If you do, many other keys for configuring
              ;; default interceptors will be ignored.
              ;; :bootstrap/interceptors []
              ::bootstrap/routes routes

              ;; Uncomment next line to enable CORS support, add
              ;; string(s) specifying scheme, host and port for
              ;; allowed source(s):
              ;;
              ;; "http://localhost:8080"
              ;;
              ;;::bootstrap/allowed-origins ["scheme://host:port"]

              ;; Root for resource interceptor that is available by default.
              ::bootstrap/resource-path "/public"

              ;; Either :jetty or :tomcat (see comments in project.clj
              ;; to enable Tomcat)
              ;;::bootstrap/host "localhost"
              ::bootstrap/type :jetty
              ::bootstrap/port 8080})
