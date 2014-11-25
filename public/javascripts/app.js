var competeWithMe = angular.module('CompeteWithMe', [
  'ngRoute',
  'ngCookies',
  'loginModule',
  'userModule',
  'challengeModule'
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
    when('/challenges', {
      templateUrl: '/assets/partials/challenges.html',
      controller: 'ChallengeCtrl'
    }).
    when('/user', {
      templateUrl: '/assets/partials/user.html',
      controller: 'UserCtrl'
    }).
    otherwise({
      redirectTo: '/login'
    });
}]).
run(['$cookies','$http', function($cookies, $http){
  var session = $cookies.session;
  if(session) {
    $http.defaults.headers.common.Authorization = 'Bearer ' + session;
  }

}]);