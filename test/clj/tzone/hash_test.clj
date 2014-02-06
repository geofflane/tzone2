(ns tzone.hash-test
  (:require [clojure.test :refer :all]
            [tzone.hash :as h]))


(deftest hash-passwords-match
  (let [orig "test"
        hashed (h/pbkdf2 orig "salt")]
  (is (not= orig hashed))
  (is (= hashed (h/pbkdf2 orig "salt")))))

(deftest hashed-with-different-salts-do-not-match
  (let [orig "test"
        hashed (h/pbkdf2 orig "salt")]
  (is (not= hashed (h/pbkdf2 orig "different")))))
