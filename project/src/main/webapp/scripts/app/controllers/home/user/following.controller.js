'use strict';

angular.module('chefsApp')
    .controller('HomeFollowingController', function ($scope, Search, ParseLinks, $stateParams, Principal, UserAPI) {
        $scope.users = [];
        $scope.page = -1;
        $scope.pageSize = 20;
        $scope.isAuthenticated = Principal.isAuthenticated;
        $scope.follower = null;

        $scope.loadAll = function() {
            if($scope.follower==null){
                Principal.identity().then(function(account) {
                    $scope.follower = account.login;
                    $scope.loadUsers(account.login, $scope.page, $scope.pageSize);
                });
            }else{
                $scope.loadUsers($scope.follower, $scope.page, $scope.pageSize);
            }

        };

        $scope.reset = function() {
            $scope.page = 0;
            $scope.users = [];
            $scope.loadAll();
        };

        $scope.loadUsers = function(follower, page, pageSize){
            UserAPI.following(follower, {
                page: page,
                size: pageSize
            }).then(function (response) {
                $scope.links = ParseLinks.parse(response.headers('link'));
                for (var i = 0; i < response.data.length; i++) {
                    $scope.users.push(response.data[i]);
                };
            });
        };

        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };

        $scope.refresh = function () {
            $scope.reset();
            $scope.clear();
        };
    });
