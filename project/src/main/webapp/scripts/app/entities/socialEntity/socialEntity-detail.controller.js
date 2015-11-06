'use strict';

angular.module('chefsApp')
    .controller('SocialEntityDetailController', function ($scope, $rootScope, $stateParams, entity, SocialEntity, Event, Recipe, SocialPicture, Tag, Competition, Assessment, Comment, User) {
        $scope.socialEntity = entity;
        $scope.load = function (id) {
            SocialEntity.get({id: id}, function(result) {
                $scope.socialEntity = result;
            });
        };
        $rootScope.$on('chefsApp:socialEntityUpdate', function(event, result) {
            $scope.socialEntity = result;
        });
    });
