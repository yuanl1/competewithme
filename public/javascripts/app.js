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
    when('/signup', {
      templateUrl: '/assets/partials/signup.html',
      controller: 'SignupCtrl'
    }).
    when('/user', {
      templateUrl: '/assets/partials/user.html',
      controller: 'UserCtrl'
    }).
    otherwise({
      redirectTo: '/login'
    });
}]);