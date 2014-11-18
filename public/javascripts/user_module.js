var userModule = angular.module('userModule', []);

loginModule.controller('UserCtrl', ['$scope', '$http', '$mdSidenav', function($scope, $http, $mdSidenav) {

  $scope.openLeftMenu = function() {
    $mdSidenav('left').toggle();
  };

}]);