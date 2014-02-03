'use strict';

/* Services */

var tzoneServices = angular.module('tzoneServices', ['ngResource']);
tzoneServices.factory('TZone', ['$resource',
  function($resource){
    return $resource('tzone');
  }]);

var convertServices = angular.module('convertServices', []);
convertServices.factory('Convert', ['$http',
    function ($http) {
      return {
        current: function(to, key) {
          return $http.get('/convertCurrent?to=' + to + '&key=' + key);
        },
        other: function(from, to, time, key) {
          return $http.get('/convertTime?from= ' + from + '&to=' + to + '&time=' + time + '&key=' + key);
        }
      }
    }]);
