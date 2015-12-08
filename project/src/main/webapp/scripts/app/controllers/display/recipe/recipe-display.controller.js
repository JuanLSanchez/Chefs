'use strict';

angular.module('chefsApp')
    .controller('RecipeDisplayController', function ($scope, $rootScope, $stateParams, entity, Recipe, Principal) {
        $scope.recipe = entity;
        Principal.identity(true).then(function(account) {
            $scope.userAccount = account;
        });
        $scope.load = function (id, message) {
            Recipe.get({id: id}, function(result) {
                $scope.recipe = result;
            });
            if($stateParams.message!=null){
                $scope.$emit('chefsApp:recipeUpdate', $stateParams.message);
            }
        };
        $rootScope.$on('chefsApp:recipeUpdate', function(event, result) {
            $scope.recipe = result;
        });
    });
