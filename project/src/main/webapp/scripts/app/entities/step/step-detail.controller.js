'use strict';

angular.module('chefsApp')
    .controller('StepDetailController', function ($scope, $rootScope, $stateParams, entity, Step, Recipe, StepPicture, Ingredient) {
        $scope.step = entity;
        $scope.load = function (id) {
            Step.get({id: id}, function(result) {
                $scope.step = result;
            });
        };
        $rootScope.$on('chefsApp:stepUpdate', function(event, result) {
            $scope.step = result;
        });
    });
