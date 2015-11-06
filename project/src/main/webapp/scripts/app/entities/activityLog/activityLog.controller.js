'use strict';

angular.module('chefsApp')
    .controller('ActivityLogController', function ($scope, ActivityLog, ParseLinks) {
        $scope.activityLogs = [];
        $scope.page = 0;
        $scope.loadAll = function() {
            ActivityLog.query({page: $scope.page, size: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                for (var i = 0; i < result.length; i++) {
                    $scope.activityLogs.push(result[i]);
                }
            });
        };
        $scope.reset = function() {
            $scope.page = 0;
            $scope.activityLogs = [];
            $scope.loadAll();
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.delete = function (id) {
            ActivityLog.get({id: id}, function(result) {
                $scope.activityLog = result;
                $('#deleteActivityLogConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            ActivityLog.delete({id: id},
                function () {
                    $scope.reset();
                    $('#deleteActivityLogConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.reset();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.activityLog = {idOfCustomer: null, nameOfCustomer: null, pictureUrl: null, objectType: null, verb: null, moment: null, name: null, description: null, tags: null, id: null};
        };
    });
