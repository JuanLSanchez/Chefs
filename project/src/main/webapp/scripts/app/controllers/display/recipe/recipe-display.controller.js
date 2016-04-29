'use strict';

angular.module('chefsApp')
    .controller('RecipeDisplayController', function ($scope, $rootScope, $stateParams, entity, Recipe, Principal) {
        $scope.recipe = entity;
        $scope.ingredients = [];

        var createListOfIngredients = function(){
            if($scope.recipe && $scope.recipe.steps){
                $scope.recipe.steps.forEach(function (step) {
                  step.ingredients.forEach(function(ingredient){
                      $scope.ingredients.push(ingredient);
                  });
                });

            }
        };

        Principal.identity(true).then(function(account) {
            $scope.userAccount = account;
        });

        $rootScope.$on('chefsApp:recipeUpdate', function(event, result) {
            $scope.recipe = result;
            createListOfIngredients();
        });

        $rootScope.$on('chefsApp:recipeNotFound', function(event, result){
            $scope.recipe = -1;
        });

        entity.$promise.then(function(){
            createListOfIngredients();
        });
    });
