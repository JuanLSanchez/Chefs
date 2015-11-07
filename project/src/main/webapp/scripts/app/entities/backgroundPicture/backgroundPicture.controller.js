'use strict';

angular.module('chefsApp')
    .controller('BackgroundPictureController', function ($scope, BackgroundPicture, ParseLinks) {
        $scope.backgroundPictures = [];
        $scope.page = 0;
        $scope.loadAll = function() {
            BackgroundPicture.query({page: $scope.page, size: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                for (var i = 0; i < result.length; i++) {
                    $scope.backgroundPictures.push(result[i]);
                }
            });
        };
        $scope.reset = function() {
            $scope.page = 0;
            $scope.backgroundPictures = [];
            $scope.loadAll();
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.delete = function (id) {
            BackgroundPicture.get({id: id}, function(result) {
                $scope.backgroundPicture = result;
                $('#deleteBackgroundPictureConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            BackgroundPicture.delete({id: id},
                function () {
                    $scope.reset();
                    $('#deleteBackgroundPictureConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.reset();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.backgroundPicture = {title: null, url: null, properties: null, id: null};
        };
    });
