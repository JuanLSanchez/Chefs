'use strict';

angular.module('chefsApp')
    .controller('EventDetailController', function ($scope, $rootScope, $stateParams, entity, Event, User, Recipe, SocialEntity) {
        $scope.event = entity;
        $scope.load = function (id) {
            Event.get({id: id}, function(result) {
                $scope.event = result;
            });
        };
        $rootScope.$on('chefsApp:eventUpdate', function(event, result) {
            $scope.event = result;
        });
    });
