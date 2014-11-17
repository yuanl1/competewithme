var userModule = angular.module('userModule', []);

loginModule.controller('UserCtrl', ['$scope', '$http', function($scope, $http) {
    $scope.email = '';
    $scope.password = '';

    $scope.doLogin = function() {
      var data = {
          email: $scope.email,
          password: $scope.password
        };

      $http.post('/api/users/login', data).
        success(function(data, status, headers, config) {

        }).error(function(data, status, headers, config) {

        });

    };

}]);