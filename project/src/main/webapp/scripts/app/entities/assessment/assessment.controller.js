'use strict';

angular.module('chefsApp')
    .controller('AssessmentController', function ($scope, Assessment, ParseLinks) {
        $scope.assessments = [];
        $scope.page = 0;
        $scope.loadAll = function() {
            Assessment.query({page: $scope.page, size: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                for (var i = 0; i < result.length; i++) {
                    $scope.assessments.push(result[i]);
                }
            });
        };
        $scope.reset = function() {
            $scope.page = 0;
            $scope.assessments = [];
            $scope.loadAll();
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.delete = function (id) {
            Assessment.get({id: id}, function(result) {
                $scope.assessment = result;
                $('#deleteAssessmentConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Assessment.delete({id: id},
                function () {
                    $scope.reset();
                    $('#deleteAssessmentConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.reset();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.assessment = {rating: null, id: null};
        };
    });
