'use strict';

angular.module('chefsApp')
    .controller('ScheduleDetailController', function ($scope, $rootScope, $stateParams, entity, Schedule, User, Menu) {
        $scope.schedule = entity;
        $scope.load = function (id) {
            Schedule.get({id: id}, function(result) {
                $scope.schedule = result;
            });
        };
        $rootScope.$on('chefsApp:scheduleUpdate', function(event, result) {
            $scope.schedule = result;
        });
    });
