'use strict';

angular.module('chefsApp')
    .controller('ActivityLogDetailController', function ($scope, $rootScope, $stateParams, entity, ActivityLog) {
        $scope.activityLog = entity;
        $scope.load = function (id) {
            ActivityLog.get({id: id}, function(result) {
                $scope.activityLog = result;
            });
        };
        $rootScope.$on('chefsApp:activityLogUpdate', function(event, result) {
            $scope.activityLog = result;
        });
    });
