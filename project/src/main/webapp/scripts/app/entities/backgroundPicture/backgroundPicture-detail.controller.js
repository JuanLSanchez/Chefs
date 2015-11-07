'use strict';

angular.module('chefsApp')
    .controller('BackgroundPictureDetailController', function ($scope, $rootScope, $stateParams, entity, BackgroundPicture, User) {
        $scope.backgroundPicture = entity;
        $scope.load = function (id) {
            BackgroundPicture.get({id: id}, function(result) {
                $scope.backgroundPicture = result;
            });
        };
        $rootScope.$on('chefsApp:backgroundPictureUpdate', function(event, result) {
            $scope.backgroundPicture = result;
        });
    });
