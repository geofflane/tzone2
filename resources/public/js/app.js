'use strict';

/* App Module */

var tzoneApp = angular.module('tzoneApp', [
  'ngRoute',
  'tzoneControllers',
  'tzoneServices',
  'convertServices'
]);

tzoneApp.config(['$routeProvider',
  function($routeProvider) {
    $routeProvider.
      otherwise({
        templateUrl: 'partials/tzone.html',
        controller: 'TzoneCtrl'
      });
  }]);
