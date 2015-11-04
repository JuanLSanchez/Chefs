'use strict';

angular.module('chefsApp')
    .controller('RequestController', function ($scope, Request, ParseLinks) {
        $scope.requests = [];
        $scope.page = 0;
        $scope.loadAll = function() {
            Request.query({page: $scope.page, size: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                for (var i = 0; i < result.length; i++) {
                    $scope.requests.push(result[i]);
                }
            });
        };
        $scope.reset = function() {
            $scope.page = 0;
            $scope.requests = [];
            $scope.loadAll();
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.delete = function (id) {
            Request.get({id: id}, function(result) {
                $scope.request = result;
                $('#deleteRequestConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Request.delete({id: id},
                function () {
                    $scope.reset();
                    $('#deleteRequestConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.reset();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.request = {creationDate: null, accepted: null, locked: null, ignored: null, id: null};
        };
    });
