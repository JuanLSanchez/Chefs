'use strict';

angular.module('chefsApp')
    .controller('ProfilePictureController', function ($scope, ProfilePicture, ParseLinks) {
        $scope.profilePictures = [];
        $scope.page = 0;
        $scope.loadAll = function() {
            ProfilePicture.query({page: $scope.page, size: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                for (var i = 0; i < result.length; i++) {
                    $scope.profilePictures.push(result[i]);
                }
            });
        };
        $scope.reset = function() {
            $scope.page = 0;
            $scope.profilePictures = [];
            $scope.loadAll();
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.delete = function (id) {
            ProfilePicture.get({id: id}, function(result) {
                $scope.profilePicture = result;
                $('#deleteProfilePictureConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            ProfilePicture.delete({id: id},
                function () {
                    $scope.reset();
                    $('#deleteProfilePictureConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.reset();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.profilePicture = {title: null, url: null, properties: null, id: null};
        };
    });
