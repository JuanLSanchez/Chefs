'use strict';

angular.module('chefsApp')
    .controller('IngredientDetailController', function ($scope, $rootScope, $stateParams, entity, Ingredient, Step, Food) {
        $scope.ingredient = entity;
        $scope.load = function (id) {
            Ingredient.get({id: id}, function(result) {
                $scope.ingredient = result;
            });
        };
        $rootScope.$on('chefsApp:ingredientUpdate', function(event, result) {
            $scope.ingredient = result;
        });
    });
