'use strict';

angular.module('chefsApp')
    .controller('ProfilePictureDetailController', function ($scope, $rootScope, $stateParams, entity, ProfilePicture, User) {
        $scope.profilePicture = entity;
        $scope.load = function (id) {
            ProfilePicture.get({id: id}, function(result) {
                $scope.profilePicture = result;
            });
        };
        $rootScope.$on('chefsApp:profilePictureUpdate', function(event, result) {
            $scope.profilePicture = result;
        });
    });
