(ns tzoneapp
  (:use [purnam.cljs :only [aget-in aset-in]])
  (:require [goog.object :as o])
  (:use-macros [purnam.js :only [obj arr ! def.n]]
               [purnam.angular :only [def.module def.config def.factory
                                      def.controller def.service]]))

(def.module tzoneApp [ngRoute ngResource])

(def.config tzoneApp [$routeProvider]
  (doto $routeProvider
    (.otherwise (obj :templateUrl "partials/tzone.html"
                     :controller "TzoneCtrl"))))

(def.factory tzoneApp.TZone [$resource]
  ($resource "tzone"))

(def.service tzoneApp.Convert [$http]
  (obj :current (fn [to key] ($http.get (str "/convertCurrent?to=" to "&key=" key)))
       :other (fn [from to time key] ($http.get (str "/convertTime?to=" to "&from=" from "&time=" time "&key=" key)))))

(def.controller tzoneApp.TzoneCtrl [$scope TZone Convert]
  (! $scope.tzones (TZone.query))

  ($scope.$watch "tzone" (fn [tzone]
                           (if (nil? tzone)
                             (! $scope.currentTime (obj :time (js/Date.) :tz "Local Time" ))
                             (->
                               (Convert.current tzone "xxx")
                               (.success (fn [data]
                                           (js/console.log data)
                                           (! $scope.currentTime data))))))))
