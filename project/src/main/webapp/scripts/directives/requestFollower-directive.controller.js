'use strict';

angular.module('chefsApp')
    .controller('RequestFollowerDirectiveController', function ($scope, Principal, RequestAPI) {
        $scope.request=null;
        $scope.isAuthenticated = Principal.isAuthenticated;

        $scope.loadAll = function() {
            if($scope.isAuthenticated()==true){
                RequestAPI.findRequestWithPrincipalAsFollowerAndFollowed(
                    $scope.user.login).then(function(result){
                        $scope.request=result.data;
                    }, function(){
                        $scope.request=false;
                    });
            }
        };

        $scope.updateFollowed = function(){
            if($scope.isAuthenticated()==true){
                RequestAPI.followed($scope.user.login).then(function(result){
                    $scope.request=result.data;
                }, function(){
                    $scope.request=false;
                });
            }
        };

        $scope.reset = function() {
            $scope.request=null;
            $scope.loadAll();
        };

        $scope.loadAll();

        $scope.refresh = function () {
            $scope.reset();
            $scope.clear();
        };
    });
