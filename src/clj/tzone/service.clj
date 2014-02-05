(ns tzone.service
  (:require [tzone.core :as core]
            [tzone.auth :as auth]
            [io.pedestal.service.http :as bootstrap]
            [io.pedestal.service.http.route :as route]
            [io.pedestal.service.http.body-params :as body-params]
            [io.pedestal.service.http.route.definition :refer [defroutes]]
            [io.pedestal.service.http.ring-middlewares :as middlewares]
            [io.pedestal.service.interceptor :refer [definterceptor]]
            [ring.middleware.session.cookie :as cookie]
            [ring.util.response :as ring-resp]))

(defn home-page
  [request]
  (ring-resp/redirect "index.html"))

(definterceptor session-interceptor
  (middlewares/session {:store (cookie/cookie-store)}))

(defroutes routes
  [[["/" {:get home-page}
     Set default interceptors for any other paths under /
     ^:interceptors [(body-params/body-params) bootstrap/json-body session-interceptor]
     ["/tzone" {:get core/tzones}]
     ["/login" {:post auth/login}]
     ["/convertCurrent"
     ^:interceptors [auth/with-apikey auth/record-usage]
     {:get core/current-time}]
     ["/convertTime"
      ^:interceptors [auth/with-apikey auth/record-usage]
      {:get core/convert-time}]]]])

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
