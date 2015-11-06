'use strict';

angular.module('chefsApp')
    .controller('SocialEntityController', function ($scope, SocialEntity, ParseLinks) {
        $scope.socialEntitys = [];
        $scope.page = 0;
        $scope.loadAll = function() {
            SocialEntity.query({page: $scope.page, size: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                for (var i = 0; i < result.length; i++) {
                    $scope.socialEntitys.push(result[i]);
                }
            });
        };
        $scope.reset = function() {
            $scope.page = 0;
            $scope.socialEntitys = [];
            $scope.loadAll();
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.delete = function (id) {
            SocialEntity.get({id: id}, function(result) {
                $scope.socialEntity = result;
                $('#deleteSocialEntityConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            SocialEntity.delete({id: id},
                function () {
                    $scope.reset();
                    $('#deleteSocialEntityConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.reset();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.socialEntity = {sumRating: null, isPublic: null, publicInscription: null, blocked: null, id: null};
        };
    });
