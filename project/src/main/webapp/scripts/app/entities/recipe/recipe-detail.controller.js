'use strict';

angular.module('chefsApp')
    .controller('RecipeDetailController', function ($scope, $rootScope, $stateParams, entity, Recipe, Competition, Vote, User, Menu, Event, SocialEntity, Step) {
        $scope.recipe = entity;
        $scope.load = function (id) {
            Recipe.get({id: id}, function(result) {
                $scope.recipe = result;
            });
        };
        $rootScope.$on('chefsApp:recipeUpdate', function(event, result) {
            $scope.recipe = result;
        });
    });
