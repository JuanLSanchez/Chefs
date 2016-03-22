'use strict';

angular.module('chefsApp')
    .controller('HomeScheduleController', function ($scope, $state, Schedule, ParseLinks) {
        $scope.schedules = [];
        $scope.page = 0;
        $scope.pageSize = 30;
        $scope.loadAll = function() {
            Schedule.query({page: $scope.page, size: $scope.pageSize}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                for (var i = 0; i < result.length; i++) {
                    $scope.schedules.push(result[i]);
                }
            });
        };
        $scope.reset = function() {
            $scope.page = 0;
            $scope.schedules = [];
            $scope.loadAll();
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.refresh = function () {
            $scope.reset();
            $scope.clear();
        };

    });
