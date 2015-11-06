'use strict';

angular.module('chefsApp')
    .controller('ScheduleController', function ($scope, Schedule, ParseLinks) {
        $scope.schedules = [];
        $scope.page = 0;
        $scope.loadAll = function() {
            Schedule.query({page: $scope.page, size: 20}, function(result, headers) {
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

        $scope.delete = function (id) {
            Schedule.get({id: id}, function(result) {
                $scope.schedule = result;
                $('#deleteScheduleConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Schedule.delete({id: id},
                function () {
                    $scope.reset();
                    $('#deleteScheduleConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.reset();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.schedule = {name: null, description: null, id: null};
        };
    });
