(ns tzone.datomic.data
  (:require [clojure.edn :as edn]
            [datomic.api :as d]))


;; TODO: This should be external
;; TODO: Probably the db and connect stuff should be shared
(def uri "datomic:free://localhost:4334/tzone")

(defn connect [] (d/connect uri))

(defn db [] (d/db (connect)))

(defn create-database []
  (d/create-database uri))

(defn install-schema [resource]
  (let [schema (slurp resource)
        schema-tx (edn/read-string {:readers *data-readers*} schema)]
    @(d/transact (connect) schema-tx)))

(defn install-user-schema [] (install-schema "resources/user-schema.edn"))

(defn install-usage-schema [] (install-schema "resources/usage-schema.edn"))

