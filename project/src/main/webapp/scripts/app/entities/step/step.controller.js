'use strict';

angular.module('chefsApp')
    .controller('StepController', function ($scope, Step, ParseLinks) {
        $scope.steps = [];
        $scope.page = 0;
        $scope.loadAll = function() {
            Step.query({page: $scope.page, size: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                for (var i = 0; i < result.length; i++) {
                    $scope.steps.push(result[i]);
                }
            });
        };
        $scope.reset = function() {
            $scope.page = 0;
            $scope.steps = [];
            $scope.loadAll();
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.delete = function (id) {
            Step.get({id: id}, function(result) {
                $scope.step = result;
                $('#deleteStepConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Step.delete({id: id},
                function () {
                    $scope.reset();
                    $('#deleteStepConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.reset();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.step = {position: null, section: null, id: null};
        };
    });
