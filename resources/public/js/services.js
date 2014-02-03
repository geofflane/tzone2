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
        current: function(tzone) {
          return $http.get('/convertCurrent?to=' + tzone + '&key=xxx');
        }
      }
    }]);
