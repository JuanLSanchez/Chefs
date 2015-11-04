'use strict';

angular.module('chefsApp')
    .controller('TagDetailController', function ($scope, $rootScope, $stateParams, entity, Tag, SocialEntity) {
        $scope.tag = entity;
        $scope.load = function (id) {
            Tag.get({id: id}, function(result) {
                $scope.tag = result;
            });
        };
        $rootScope.$on('chefsApp:tagUpdate', function(event, result) {
            $scope.tag = result;
        });
    });
