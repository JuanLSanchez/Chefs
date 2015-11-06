'use strict';

angular.module('chefsApp')
    .controller('MenuController', function ($scope, Menu, ParseLinks) {
        $scope.menus = [];
        $scope.page = 0;
        $scope.loadAll = function() {
            Menu.query({page: $scope.page, size: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                for (var i = 0; i < result.length; i++) {
                    $scope.menus.push(result[i]);
                }
            });
        };
        $scope.reset = function() {
            $scope.page = 0;
            $scope.menus = [];
            $scope.loadAll();
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.delete = function (id) {
            Menu.get({id: id}, function(result) {
                $scope.menu = result;
                $('#deleteMenuConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Menu.delete({id: id},
                function () {
                    $scope.reset();
                    $('#deleteMenuConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.reset();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.menu = {time: null, id: null};
        };
    });
