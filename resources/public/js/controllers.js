'use strict';

var tzoneControllers = angular.module('tzoneControllers', []);

tzoneControllers.controller('TzoneCtrl', ['$scope', 'TZone', 'Convert',
  function($scope, TZone, Convert) {
    $scope.tzones = TZone.query();

    $scope.$watch('tzone', function() {
      if ($scope.tzone) {
        Convert.current($scope.tzone, 'xxx').success(function (data) {
          $scope.currentTime = data;
        });
      } else {
        $scope.currentTime = {"time": new Date(), "tz": "Local Time"};
      }
    });
  }]);

