'use strict';

angular.module('chefsApp')
    .controller('HomeFollowingController', function ($scope, Search, ParseLinks, $stateParams, Principal, UserAPI) {
        $scope.users = [];
        $scope.page = -1;
        $scope.pageSize = 20;
        $scope.isAuthenticated = Principal.isAuthenticated;
        $scope.followed = null;

        $scope.loadAll = function() {
            if($scope.followed==null){
                Principal.identity().then(function(account) {
                    $scope.followed = account.login;
                    var page=$scope.page;
                    var pageSize=$scope.pageSize;
                    $scope.loadUsers(account.login, page, pageSize);
                });
            }else{
                $scope.loadUsers($scope.followed, $scope.page, $scope.pageSize);
            }

        };

        $scope.reset = function() {
            $scope.page = 0;
            $scope.users = [];
            $scope.loadAll();
        };

        $scope.loadUsers = function(followed, page, pageSize){
            UserAPI.following(followed, {
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
