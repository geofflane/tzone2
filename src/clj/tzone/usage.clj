(ns tzone.usage
  (:require [tzone.datomic.data :as data]
            [datomic.api :as d]
            [clj-time.local :refer (local-now)]
            [clj-time.coerce :refer (to-date)]))

(defn record-usage [apikey service]
  @(d/transact (data/connect)
              [{:db/id (d/tempid :db.part/user)
                :usage/apikey apikey
                :usage/timestamp (to-date (local-now))
                :usage/service service}]))

