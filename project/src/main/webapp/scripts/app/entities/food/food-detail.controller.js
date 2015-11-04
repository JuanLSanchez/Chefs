'use strict';

angular.module('chefsApp')
    .controller('FoodDetailController', function ($scope, $rootScope, $stateParams, entity, Food, Ingredient) {
        $scope.food = entity;
        $scope.load = function (id) {
            Food.get({id: id}, function(result) {
                $scope.food = result;
            });
        };
        $rootScope.$on('chefsApp:foodUpdate', function(event, result) {
            $scope.food = result;
        });
    });
