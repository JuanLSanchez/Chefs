'use strict';

angular.module('chefsApp')
    .controller('SocialPictureDetailController', function ($scope, $rootScope, $stateParams, entity, SocialPicture, SocialEntity) {
        $scope.socialPicture = entity;
        $scope.load = function (id) {
            SocialPicture.get({id: id}, function(result) {
                $scope.socialPicture = result;
            });
        };
        $rootScope.$on('chefsApp:socialPictureUpdate', function(event, result) {
            $scope.socialPicture = result;
        });
    });
