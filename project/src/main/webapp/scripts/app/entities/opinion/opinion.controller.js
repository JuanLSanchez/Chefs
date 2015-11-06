'use strict';

angular.module('chefsApp')
    .controller('OpinionController', function ($scope, Opinion, ParseLinks) {
        $scope.opinions = [];
        $scope.page = 0;
        $scope.loadAll = function() {
            Opinion.query({page: $scope.page, size: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                for (var i = 0; i < result.length; i++) {
                    $scope.opinions.push(result[i]);
                }
            });
        };
        $scope.reset = function() {
            $scope.page = 0;
            $scope.opinions = [];
            $scope.loadAll();
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.delete = function (id) {
            Opinion.get({id: id}, function(result) {
                $scope.opinion = result;
                $('#deleteOpinionConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Opinion.delete({id: id},
                function () {
                    $scope.reset();
                    $('#deleteOpinionConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.reset();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.opinion = {name: null, minValue: null, maximum: null, id: null};
        };
    });
