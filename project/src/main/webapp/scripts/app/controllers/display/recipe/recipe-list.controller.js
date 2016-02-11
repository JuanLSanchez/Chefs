'use strict';

angular.module('chefsApp')
    .controller('RecipeListController', function ($scope, $state, $stateParams, Search, ParseLinks) {
        $scope.recipes = [];
        $scope.page = 0;
        $scope.pageSize = 4;
        $scope.loadAll = function() {
            var q = $stateParams.q;
            Search.recipesList(q, {page: $scope.page, size: $scope.pageSize}).then(function(response){
                $scope.links = ParseLinks.parse(response.headers('link'));
                for (var i = 0; i < response.data.length; i++) {
                    $scope.recipes.push(response.data[i]);
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
        $scope.showRecipe = function (param) {
            $state.go("ChefRecipeDisplay", param);
        };
    });
