(ns tzone.service-test
  (:require [clojure.test :refer :all]
            [io.pedestal.service.test :refer :all]
            [io.pedestal.service.http :as bootstrap]
            [tzone.service :as service]))

(def service
  (::bootstrap/service-fn (bootstrap/create-servlet service/service)))

(deftest home-page-test
  (is (= (:status (response-for service :get "/")) 302))
  (is (=
       (get-in (response-for service :get "/index.html") [:headers "Content-Type"])
       "text/html")))


