'use strict';

angular.module('chefsApp')
    .controller('RequestCountDirectiveController', function ($scope, RequestAPI, $state) {
        $scope.requestInfo=null;

        $scope.loadAll = function() {
            RequestAPI.requestInfo($scope.user)
                .then(function(result){
                    $scope.requestInfo=result.data;
                });
        };

        $scope.reset = function() {
            $scope.requestInfo=null;
            $scope.loadAll();
        };

        $scope.loadAll();

        $scope.refresh = function () {
            $scope.reset();
            $scope.clear();
        };

        $scope.showFollowing = function(){
            if ($scope.redirect == 'home'){
                $state.go('HomeFollowing');
            }else{
                $state.go('ChefFollowing', {login:$scope.user});
            }
        };

        $scope.showFollowers = function(){
            if ($scope.redirect == 'home'){
                $state.go('HomeFollowers');
            }else{
                $state.go('ChefFollowers', {login:$scope.user});
            }
        };

        $scope.showRecipes = function(){
            if ($scope.redirect == 'home'){
                $state.go('HomeRecipes');
            }else{
                $state.go('ChefRecipes', {login:$scope.user});
            }
        };

        $scope.$watch('user', function(){
            $scope.loadAll();
        });
    });
