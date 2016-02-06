'use strict';

angular.module('chefsApp')
    .controller('MainController', function ($scope, Principal) {
        Principal.identity().then(function(account) {
            $scope.account = account;
            $scope.isAuthenticated = Principal.isAuthenticated;
        });
    });
angular.module('chefsApp')
    .controller('MainControllerLogin', function ($scope, Principal, $state) {
        Principal.identity().then(function(account) {
            $scope.account = account;
            $scope.isAuthenticated = Principal.isAuthenticated;
            $scope.authenticationError = true;
        }).then(function (){
            $state.go('HomeRecipes');
        });
    });
