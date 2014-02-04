(ns tzoneapp
  (:use [purnam.cljs :only [aget-in aset-in]])
  (:require [goog.object :as o])
  (:use-macros [purnam.js :only [obj arr ! def.n]]
               [purnam.angular :only [def.module def.config def.factory
                                      def.controller def.service]]))

(def.module tzoneApp [ngRoute ngResource ui.bootstrap])

(def.config tzoneApp [$routeProvider]
  (doto $routeProvider
    (.when "/current" (obj :templateUrl "partials/current.html" :controller "CurrentCtrl"))
    (.when "/convert" (obj :templateUrl "partials/convert.html" :controller "ConvertCtrl"))
    (.otherwise (obj :templateUrl "partials/index.html"))))

(def.factory tzoneApp.TZone [$resource]
  ($resource "tzone"))

(def.service tzoneApp.Convert [$http]
  (obj :current (fn [to key] ($http.get (str "/convertCurrent?to=" to "&key=" key)))
       :other (fn [from to time key] ($http.get (str "/convertTime?to=" to "&from=" from "&time=" time "&key=" key)))))

(def.controller tzoneApp.CurrentCtrl [$scope TZone Convert]
  (! $scope.tzones (TZone.query))
  (! $scope.currentTime (obj :time (js/Date.) :tz "Local Time" ))
  (! $scope.convert (fn [totz]
                      (->
                        (Convert.current totz "xxx")
                        (.success (fn [data] (! $scope.currentTime data)))))))

(def.controller tzoneApp.ConvertCtrl [$scope TZone Convert dateFilter]
  (! $scope.tzones (TZone.query))
  (! $scope.currentTime (obj :time (js/Date.) :tz "Local Time" ))
  (! $scope.time (dateFilter (js/Date.) "yyyy-MM-dd'T'hh:mm:ss.sss0"))
  (! $scope.convert (fn [fromtz totz time]
                      (->
                        (Convert.other fromtz totz time "xxx")
                        (.success (fn [data] (! $scope.currentTime data)))))))
