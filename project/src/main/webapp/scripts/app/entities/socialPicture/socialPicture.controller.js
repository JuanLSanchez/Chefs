'use strict';

angular.module('chefsApp')
    .controller('SocialPictureController', function ($scope, SocialPicture, ParseLinks) {
        $scope.socialPictures = [];
        $scope.page = 0;
        $scope.loadAll = function() {
            SocialPicture.query({page: $scope.page, size: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.socialPictures = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.delete = function (id) {
            SocialPicture.get({id: id}, function(result) {
                $scope.socialPicture = result;
                $('#deleteSocialPictureConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            SocialPicture.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteSocialPictureConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.socialPicture = {title: null, url: null, properties: null, id: null};
        };
    });
