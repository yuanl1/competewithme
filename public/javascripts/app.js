var competeWithMe = angular.module('CompeteWithMe', [
  'ngRoute',
  'ngMaterial',
  'loginModule',
  'userModule'
]);

competeWithMe.config(['$routeProvider', function($routeProvider) {
  $routeProvider.
    when('/login', {
      templateUrl: '/assets/partials/login.html',
      controller: 'LoginCtrl'
    }).
    when('/user', {
      templateUrl: '/assets/partials/user.html',
      controller: 'UserCtrl'
    }).
    otherwise({
      redirectTo: '/login'
    });
}]);