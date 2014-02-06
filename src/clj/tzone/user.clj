(ns tzone.user
  (:require [datomic.api :as d]
            [tzone.datomic.data :as data]))

(defn add-user [username password-hash email apikey]
  @(d/transact (data/connect)
              [{:db/id (d/tempid :db.part/user)
                :user/username username
                :user/password password-hash
                :user/apikey apikey
                :user/email email}]))

(defn validate-apikey [apikey]
  (ffirst (d/q '[:find ?e
                 :in $ ?apikey
                 :where [?e :user/apikey ?apikey]]
               (data/db)
               apikey)))

(defn find-user-id [username]
  (ffirst (d/q '[:find ?e
                 :in $ ?username
                 :where [?e :user/username ?username]]
               (data/db)
               username)))

(defn get-user [username]
  "Get a user by username"
  (let [user-id (find-user-id username)]
    (when-not (nil? user-id)
     (when-let [user (d/entity (data/db) user-id)]
       (d/touch user)))))
