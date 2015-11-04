'use strict';

angular.module('chefsApp')
    .controller('TagController', function ($scope, Tag, ParseLinks) {
        $scope.tags = [];
        $scope.page = 0;
        $scope.loadAll = function() {
            Tag.query({page: $scope.page, size: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                for (var i = 0; i < result.length; i++) {
                    $scope.tags.push(result[i]);
                }
            });
        };
        $scope.reset = function() {
            $scope.page = 0;
            $scope.tags = [];
            $scope.loadAll();
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.delete = function (id) {
            Tag.get({id: id}, function(result) {
                $scope.tag = result;
                $('#deleteTagConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Tag.delete({id: id},
                function () {
                    $scope.reset();
                    $('#deleteTagConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.reset();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.tag = {name: null, id: null};
        };
    });
