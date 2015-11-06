'use strict';

angular.module('chefsApp')
    .controller('StepPictureController', function ($scope, StepPicture, ParseLinks) {
        $scope.stepPictures = [];
        $scope.page = 0;
        $scope.loadAll = function() {
            StepPicture.query({page: $scope.page, size: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                for (var i = 0; i < result.length; i++) {
                    $scope.stepPictures.push(result[i]);
                }
            });
        };
        $scope.reset = function() {
            $scope.page = 0;
            $scope.stepPictures = [];
            $scope.loadAll();
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.delete = function (id) {
            StepPicture.get({id: id}, function(result) {
                $scope.stepPicture = result;
                $('#deleteStepPictureConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            StepPicture.delete({id: id},
                function () {
                    $scope.reset();
                    $('#deleteStepPictureConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.reset();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.stepPicture = {title: null, url: null, properties: null, id: null};
        };
    });
