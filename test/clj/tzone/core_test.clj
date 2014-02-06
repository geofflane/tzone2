(ns tzone.core-test
  (:require [clojure.test :refer :all]
            [tzone.core :as core]))

(deftest tzones-test
  (is (not (empty? (core/tzones {})))))

;; TODO: These tests are all time dependent. Not totally sure what to test
(deftest current-tzone-test
  (let [{:keys [body]} (core/current-time {:params {:to "America/Los_Angeles"}})]
    (is (not (empty? body)))
    (is (= (:tz body) "America/Los_Angeles"))
    (is (not (nil? (:time body))))))

(deftest convert-tzone-test
  (let [{:keys [body]} (core/convert-time {:params {:from "America/Chicago"
                                                :to "America/Los_Angeles"
                                                :time "2014-02-05T09:15:00.0000"}})]
    (is (not (empty? body)))
    (is (= (:tz body) "America/Los_Angeles"))
    (is (not (nil? (:time body))))))

