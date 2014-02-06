(ns tzone.service-test
  (:require [clojure.test :refer :all]
            [io.pedestal.service.test :refer :all]
            [io.pedestal.service.http :as bootstrap]
            [tzone.service :as service]
            [tzone.user :as usr]
            [tzone.usage :as usage]))

(def service
  (::bootstrap/service-fn (bootstrap/create-servlet service/service)))

(deftest home-page-test
  (is (= (:status (response-for service :get "/")) 302))
  (is (=
       (get-in (response-for service :get "/index.html") [:headers "Content-Type"])
       "text/html")))

(deftest validates-valid-apikey
  (with-redefs [usr/validate-apikey (fn [apikey] true)]
    (let [response (response-for service :get "/convertCurrent?to=America/Chicago&key=foo")]
      (is (not (re-matches #".*error.*" (:body response)))))))

(deftest no-apikey-returns-error
  (with-redefs [usr/validate-apikey (fn [apikey] nil)
                usage/record-usage (fn [apikey service] nil)]
    (let [response (response-for service :get "/convertCurrent?to=America/Chicago")]
      (is (= (:body response) "{\"error\":\"Must pass API key\"}")))))

(deftest invalid-apikey-returns-error
  (with-redefs [usr/validate-apikey (fn [apikey] nil)
                usage/record-usage (fn [apikey service] nil)]
    (let [response (response-for service :get "/convertCurrent?to=America/Chicago&key=foo")]
      (is (= (:body response) "{\"error\":\"Unknown API key\"}")))))
