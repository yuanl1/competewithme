var loginModule = angular.module('loginModule', ['ngCookies']);

loginModule.controller('LoginCtrl', [
  '$scope',
  '$http',
  '$cookies',
  '$location',
  '$mdToast',
function($scope, $http, $cookies, $location, $mdToast) {
    $scope.email = '';
    $scope.password = '';

    $scope.doLogin = function() {
      var data = {
          email: $scope.email,
          password: $scope.password
        };

      $http.post('/api/users/login', data).
        success(function(data, status, headers, config) {
          $cookies.session = data.id;
          $location.url('/user');
        }).error(function(data, status, headers, config) {
          $mdToast.show({
            template: '<md-toast>Incorrect User or Password</md-toast>',
            hideDelay: 2000,
            position: 'top left right'
          });
        });

    };

}]);