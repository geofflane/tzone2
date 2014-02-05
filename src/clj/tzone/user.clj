(ns tzone.user
  (:require [clojure.edn :as edn]
            [datomic.api :as d]
            [clj-time.local :refer (local-now)]
            [clj-time.coerce :refer (to-date)]))

;; TODO: This should be external
;; TODO: Probably the db and connect stuff should be shared
(def uri "datomic:free://localhost:4334/tzone")

(defn connect [] (d/connect uri))

(defn create-database []
  (d/create-database uri))

(defn install-schema []
  (let [schema (slurp "resources/user-schema.edn")
        schema-tx (edn/read-string {:readers *data-readers*} schema)]
    @(d/transact (connect) schema-tx)))

(defn add-user [username password-hash email apikey]
  @(d/transact (connect)
              [{:db/id (d/tempid :db.part/user)
                :user/username username
                :user/password password-hash
                :user/apikey apikey
                :user/email email}]))

(defn validate-apikey [apikey]
  (ffirst (d/q '[:find ?e
                 :in $ ?apikey
                 :where [?e :user/apikey ?apikey]]
               (d/db (connect))
               apikey)))

(defn find-user-id [username]
  (ffirst (d/q '[:find ?e
                 :in $ ?username
                 :where [?e :user/username ?username]]
               (d/db (connect))
               username)))

(defn get-user [username]
  "Get a user by username"
  (let [user-id (find-user-id username)]
    (when-not (nil? user-id)
     (when-let [user (d/entity (d/db (connect)) user-id)] 
       (d/touch user)))))
