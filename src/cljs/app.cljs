(ns tzoneapp
  (:use [purnam.cljs :only [aget-in aset-in]])
  (:require [goog.object :as o])
  (:use-macros [purnam.js :only [obj arr ! def.n]]
               [purnam.angular :only [def.module def.config def.factory def.constant
                                      def.controller def.service]]))

(def.module tzoneApp [ngCookies ngRoute ngResource ui.bootstrap])

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

(def.constant tzoneApp.CookieName "tzone-login")

(def.service tzoneApp.User [$http $cookieStore CookieName]
  ;; TODO: Remove "xxx" key when cookies are working
  (obj :cookie (fn [] (or ($cookieStore.get CookieName) {}))
       :apikey  (fn [] (or (.-apikey (self.cookie)) "xxx"))
       :username (fn [] (.-username (self.cookie)))
       :loggedin (fn [] (not (nil? (self.username))))
       :login (fn [user] ($http.post "/login" user))
       :logout (fn [] ($cookieStore.remove CookieName))))

(def.controller tzoneApp.NavigationCtrl [$scope $location]
  (! $scope.isCurrentPath  (fn [path] (= ($location.path) path))))

(def.controller tzoneApp.CurrentCtrl [$scope TZone Convert User]
  (! $scope.tzones (TZone.query))
  (! $scope.currentTime (obj :time (js/Date.) :tz "Local Time" ))
  (! $scope.convert (fn [totz]
                      (->
                        (Convert.current totz (User.apikey))
                        (.success (fn [data] (! $scope.currentTime data)))))))

(def.controller tzoneApp.ConvertCtrl [$scope TZone Convert User dateFilter]
  (! $scope.tzones (TZone.query))
  (! $scope.currentTime (obj :time (js/Date.) :tz "Local Time" ))
  (! $scope.time (dateFilter (js/Date.) "yyyy-MM-dd'T'hh:mm:ss.sss0"))
  (! $scope.convert (fn [fromtz totz time]
                      (->
                        (Convert.other fromtz totz time (User.apikey))
                        (.success (fn [data] (! $scope.currentTime data)))))))

(def.controller tzoneApp.LoginCtrl [$scope $modalInstance $timeout User]
  (! $scope.login (fn [user]
                    (->
                      (User.login user)
                      (.success (fn [data]
                                  (if data.login
                                    (do (! $scope.message "Login successful")
                                        ($timeout (fn [] ($modalInstance.close data)) 2000))
                                    (! $scope.message "Invalid username or password"))))
                      (.error (fn [] (! $scope.message "Unknown error logging in.")))))))

(def.controller tzoneApp.AuthCtrl [$scope $modal User]
  ;; Initial values come from User if their available
  ;; Later on we'll override these if the user logs in, for example
  (! $scope.username (User.username))

  (! $scope.setUser (fn [user]
                      (! $scope.username user.username)))
  (! $scope.open (fn []
                   (let [modalInstance ($modal.open (obj :templateUrl "partials/login.html" :controller "LoginCtrl"))]
                     (modalInstance.result.then (fn [user] ($scope.setUser user))
                                                (fn [] (js/console.log "Modal dismissed"))))))
  (! $scope.logout (fn []
                     (User.logout)
                     (! $scope.username nil))))


