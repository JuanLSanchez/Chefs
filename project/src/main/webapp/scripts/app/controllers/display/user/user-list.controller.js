'use strict';

angular.module('chefsApp')
    .controller('UserListController', function ($scope, Search, ParseLinks, $stateParams, Principal, RequestAPI) {
        $scope.users = [];
        $scope.page = 0;
        $scope.pageSize = 20;
        $scope.isAuthenticated = Principal.isAuthenticated;
        $scope.loadAll = function() {
            var q = $stateParams.q;
            Search.usersList(q, {page: $scope.page, size: $scope.pageSize}).then(function(response){
                $scope.links = ParseLinks.parse(response.headers('link'));
                for (var i = 0; i < response.data.length; i++) {
                    $scope.users.push(response.data[i]);
                };
            });
        };
        $scope.reset = function() {
            $scope.page = 0;
            $scope.recipes = [];
            $scope.loadAll();
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
    });
