'use strict';

angular.module('chefsApp')
    .controller('IngredientController', function ($scope, Ingredient, ParseLinks) {
        $scope.ingredients = [];
        $scope.page = 0;
        $scope.loadAll = function() {
            Ingredient.query({page: $scope.page, size: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                for (var i = 0; i < result.length; i++) {
                    $scope.ingredients.push(result[i]);
                }
            });
        };
        $scope.reset = function() {
            $scope.page = 0;
            $scope.ingredients = [];
            $scope.loadAll();
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.delete = function (id) {
            Ingredient.get({id: id}, function(result) {
                $scope.ingredient = result;
                $('#deleteIngredientConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Ingredient.delete({id: id},
                function () {
                    $scope.reset();
                    $('#deleteIngredientConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.reset();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.ingredient = {amount: null, measurement: null, id: null};
        };
    });
