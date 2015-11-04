'use strict';

angular.module('chefsApp')
    .controller('FoodController', function ($scope, Food, ParseLinks) {
        $scope.foods = [];
        $scope.page = 0;
        $scope.loadAll = function() {
            Food.query({page: $scope.page, size: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                for (var i = 0; i < result.length; i++) {
                    $scope.foods.push(result[i]);
                }
            });
        };
        $scope.reset = function() {
            $scope.page = 0;
            $scope.foods = [];
            $scope.loadAll();
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.delete = function (id) {
            Food.get({id: id}, function(result) {
                $scope.food = result;
                $('#deleteFoodConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Food.delete({id: id},
                function () {
                    $scope.reset();
                    $('#deleteFoodConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.reset();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.food = {normalizaedName: null, name: null, kcal: null, id: null};
        };
    });
