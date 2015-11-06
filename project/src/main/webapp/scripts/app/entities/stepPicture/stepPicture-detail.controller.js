'use strict';

angular.module('chefsApp')
    .controller('StepPictureDetailController', function ($scope, $rootScope, $stateParams, entity, StepPicture, Step) {
        $scope.stepPicture = entity;
        $scope.load = function (id) {
            StepPicture.get({id: id}, function(result) {
                $scope.stepPicture = result;
            });
        };
        $rootScope.$on('chefsApp:stepPictureUpdate', function(event, result) {
            $scope.stepPicture = result;
        });
    });
