'use strict';

angular.module('chefsApp')
    .controller('ChefFollowersController', function ($scope, $stateParams, Search, ParseLinks, Principal, UserAPI) {
        $scope.users = [];
        $scope.page = 0;
        $scope.pageSize = 20;
        $scope.isAuthenticated = Principal.isAuthenticated;
        $scope.followed = null;

        $scope.followed = $stateParams.login;

        $scope.loadAll = function() {
            $scope.loadUsers($scope.followed);
        };

        $scope.reset = function() {
            $scope.page = 0;
            $scope.users = [];
            $scope.loadAll();
        };

        $scope.loadUsers = function(followed){
            UserAPI.followers(followed, {
                page: $scope.page,
                size: $scope.pageSize
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

        $scope.loadAll();

        $scope.refresh = function () {
            $scope.reset();
            $scope.clear();
        };
// Redirect followers and following
        $scope.redirect = function(){
            return 'chef';
        }
    });
