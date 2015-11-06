'use strict';

angular.module('chefsApp')
    .controller('RecipeController', function ($scope, Recipe, ParseLinks) {
        $scope.recipes = [];
        $scope.page = 0;
        $scope.loadAll = function() {
            Recipe.query({page: $scope.page, size: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                for (var i = 0; i < result.length; i++) {
                    $scope.recipes.push(result[i]);
                }
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

        $scope.delete = function (id) {
            Recipe.get({id: id}, function(result) {
                $scope.recipe = result;
                $('#deleteRecipeConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Recipe.delete({id: id},
                function () {
                    $scope.reset();
                    $('#deleteRecipeConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.reset();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.recipe = {name: null, description: null, creationDate: null, informationUrl: null, advice: null, sugestedTime: null, updateDate: null, ingredientsInSteps: null, id: null};
        };
    });
