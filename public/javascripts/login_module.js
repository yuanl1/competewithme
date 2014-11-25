var loginModule = angular.module('loginModule', ['ngCookies', 'ngMaterial']);
loginModule.value('init', {'email' : ''});

loginModule.controller('LoginCtrl', [
  '$scope',
  '$http',
  '$cookies',
  '$location',
  '$mdToast',
  'init',
function($scope, $http, $cookies, $location, $mdToast, init) {
    $scope.email = init.email;
    $scope.password = '';

    $scope.doLogin = function() {
      var data = {
          email: $scope.email,
          password: $scope.password
        };

      $http.post('/api/users/login', data).
        success(function(data, status, headers, config) {
          $cookies.session = data.id;
          $http.defaults.headers.common.Authorization = 'Bearer ' + data.id;
          $location.url('/challenges');
        }).error(function(data, status, headers, config) {
          $mdToast.show({
            template: '<md-toast>Incorrect Email or Password</md-toast>',
            hideDelay: 2000,
            position: 'top left right'
          });
        });

    };

    $scope.doSignup = function() {
      $location.url('/signup');
    };


}]);

loginModule.controller('SignupCtrl', [
  '$scope',
  '$http',
  '$cookies',
  '$location',
  '$mdToast',
  'init',
function($scope, $http, $cookies, $location, $mdToast, init){
  $scope.email = '';
  $scope.password = '';
  $scope.name = '';

  $scope.doSignup = function() {
    var data = {
      name : $scope.name,
      email : $scope.email,
      password : $scope.password
    };

    $http.post('/api/users/register', data).
      success(function(data, status, headers, config){
          $cookies.session = data.id;
          $location.url('/challenges');
      }).error(function(data, status, headers, config){
          if(status === 409) {
            init.email = $scope.email;
            $location.url('/login');
            $mdToast.show({
              template: '<md-toast>The email provided is already registered</md-toast>',
              hideDelay: 2000,
              position: 'top left right'
            });
          } else {
            $mdToast.show({
              template: '<md-toast>Validation requirements not met</md-toast>',
              hideDelay: 2000,
              position: 'top left right'
            });
          }

      });
  };

  $scope.doLogin = function() {
    $location.url('login');
  };


}]);
