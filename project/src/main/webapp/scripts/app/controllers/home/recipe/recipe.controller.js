'use strict';

angular.module('chefsApp')
    .controller('HomeRecipeController', function ($scope, RecipeUser, ParseLinks) {
        $scope.recipes = [];
        $scope.page = 0;
        $scope.loadAll = function() {
            RecipeUser.get({page: $scope.page, size: 10}, function(result, headers) {
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

        $scope.refresh = function () {
            $scope.reset();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.recipe = {name: null, description: null, creationDate: null, informationUrl: null, advice: null, sugestedTime: null, updateDate: null, ingredientsInSteps: null, id: null};
        };
    });
