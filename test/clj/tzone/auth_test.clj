(ns tzone.auth-test
  (:require [clojure.test :refer :all]
            [tzone.user :as usr]
            [tzone.hash :as hash]
            [tzone.auth :as auth]))

;; NOTE: with-redefs used to stub out actual interactions with user storage

(def request {:json-params {:username "geoff" :password "secret"}})
(def request-with-bad-pass  (assoc-in request [:json-params :password] "bad"))
(def good-db-user {:user/username "geoff" :user/password (hash/pbkdf2 "secret" "geoff") :user/apikey "foo"})

(deftest login-finds-user
  (with-redefs [usr/get-user (fn [un] good-db-user)]
    (let [response (auth/login request)]
      (is (not (nil? response)))
      (is (get-in response [:body :login]))
      (is (= (get-in response [:body :apikey]) "foo")))))

(deftest login-does-not-find-user
  (with-redefs [usr/get-user (fn [un] nil)]
    (let [response (auth/login request)]
      (is (not (nil? response)))
      (is (not (get-in response [:body :login]))))))


(deftest login-finds-user-but-passwords-do-not-match
  (with-redefs [usr/get-user (fn [un] good-db-user)]
    (let [response (auth/login request-with-bad-pass)]
      (is (not (nil? response)))
      (is (not (get-in response [:body :login]))))))


