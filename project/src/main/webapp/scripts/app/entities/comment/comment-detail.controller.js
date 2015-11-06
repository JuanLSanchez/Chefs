'use strict';

angular.module('chefsApp')
    .controller('CommentDetailController', function ($scope, $rootScope, $stateParams, entity, Comment, User, SocialEntity) {
        $scope.comment = entity;
        $scope.load = function (id) {
            Comment.get({id: id}, function(result) {
                $scope.comment = result;
            });
        };
        $rootScope.$on('chefsApp:commentUpdate', function(event, result) {
            $scope.comment = result;
        });
    });
