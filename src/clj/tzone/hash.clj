(ns tzone.hash
  (:require [ring.util.codec :as codec]) ;; Not great to use ring since that ties us to Web :(
  (:import (java.math BigInteger)
           (java.security SecureRandom)
           (javax.crypto SecretKeyFactory)
           (javax.crypto.spec PBEKeySpec)))

;; Assuming this is thread safe and sharable
(def seckey-fac (SecretKeyFactory/getInstance "PBKDF2WithHmacSHA1"))

(defn pbkdf2
  "Get a hash for the given string and optional salt"
  ([orig salt]
   (let [k (PBEKeySpec. (char-array orig) (.getBytes salt) 1000 192)]
     (codec/base64-encode (.getEncoded (.generateSecret seckey-fac k))))))
