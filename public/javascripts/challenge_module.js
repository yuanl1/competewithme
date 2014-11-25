var userModule = angular.module('challengeModule', ['ngCookies','ngMaterial']);

loginModule.controller('ChallengeCtrl', [
  '$scope',
  '$http',
  '$location',
  '$cookies',
  '$mdSidenav',
  '$mdToast',
function($scope, $http, $location, $cookies, $mdSidenav, $mdToast) {

  $http.get('/api/users/challenges').
      success(function(data, status, headers, config){

      }).error(function(data, status, headers, config){
        if (status === 401) {
          $location.url('/login');
        } else {
          $mdToast.show({
            template: '<md-toast>Unable to retrieve challenge data</md-toast>',
            hideDelay: 2000,
            position: 'top left right'
          });
        }

      });

  $scope.openLeftMenu = function() {
    $mdSidenav('left').toggle();
  };

}]);