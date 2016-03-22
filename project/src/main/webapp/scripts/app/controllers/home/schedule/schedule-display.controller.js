'use strict';

angular.module('chefsApp')
    .controller('ScheduleDisplayController', function ($scope, $rootScope, $stateParams, entity, Principal, Schedule) {
        $scope.schedule = entity;
        Principal.identity(true).then(function(account) {
            $scope.userAccount = account;
        });
        $scope.load = function (id, message) {
            Schedule.get({id: id}, function(result) {
                $scope.schedule = result;
            });
            if($stateParams.message!=null){
                $scope.$emit('chefsApp:scheduleUpdate', $stateParams.message);
            }
        };
        $rootScope.$on('chefsApp:scheduleUpdate', function(event, result) {
            $scope.schedule = result;
        });
    });
