(ns tzone.auth
  (:require [tzone.user :as usr]
            [tzone.hash :as hash]
            [ring.util.response :as ring-response]
            [io.pedestal.service.http :as bootstrap]
            [io.pedestal.service.interceptor :refer [defbefore]]
            [cheshire.core :as json]))

(defn valid [apikey]
  (not (nil? (usr/validate-apikey apikey))))

(defn stop-and-respond [context value]
  (assoc context :response (bootstrap/json-response value)))

;; (defn- apikey-for [context] (-> context :request :params :key))
(defn- apikey-for [context] (get-in context [:request :params :key]))

(defbefore with-apikey [context]
  "Security interceptor"
  (let [apikey (apikey-for context)]
    (cond
      (nil? apikey) (stop-and-respond context {:error "Must pass API key"})
      (not (valid apikey)) (stop-and-respond context {:error "Unknown API key"})
      :otherwise context)))

(defbefore record-usage [context]
  (let [apikey (apikey-for context)]
    ;; TODO: Implement this with a real DB
    (println "ApiKey used: " apikey)
    context))

(defn- password-match [dbuser check]
  (= (hash/pbkdf2 check (:user/username dbuser)) (:user/password dbuser)))

(defn- authenticate [username password]
  (let [dbuser (usr/get-user username)]
    (when-not (nil? dbuser)
      (when (password-match dbuser password)
        {:username username :apikey (:user/apikey dbuser)}))))


(defn login [request]
  (let [{:keys [username password]} (:json-params request)
        user (authenticate username password)]
    (if (nil? user)
      (ring-response/response {:login false})
      (-> (ring-response/response (assoc user :login true))
          (ring-response/set-cookie "tzone-login" (json/generate-string user))))))
