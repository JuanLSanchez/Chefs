'use strict';

angular.module('chefsApp')
    .controller('RequestFollowedDirectiveController', function ($scope, Principal, RequestAPI) {
        $scope.request=null;
        $scope.isAuthenticated = Principal.isAuthenticated;

        $scope.loadAll = function() {
            if($scope.isAuthenticated()==true){
                RequestAPI.findRequestWithPrincipalAsFollowedAndFollower(
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

        $scope.acceptFollower = function(){
            RequestAPI.follower($scope.user.login, 'accepted').then(function(result){
                $scope.request=result.data;
                $scope.updateCount();
            });
        };

        $scope.lockedFollower = function(){
            RequestAPI.follower($scope.user.login, 'locked').then(function(result){
                $scope.request=result.data;
                $scope.updateCount();
            });
        };

        $scope.ignoreFollower = function(){
            RequestAPI.follower($scope.user.login, 'ignored').then(function(result){
                $scope.request=result.data;
                $scope.updateCount();
            });
        }

        $scope.updateCount = function(){
            RequestAPI.setUpdateCount($scope.user.login, true);
            Principal.identity().then(function(account) {
                RequestAPI.setUpdateCount(account.login, true);
            });
        };
    });
