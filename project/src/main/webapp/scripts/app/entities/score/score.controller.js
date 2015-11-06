'use strict';

angular.module('chefsApp')
    .controller('ScoreController', function ($scope, Score, ParseLinks) {
        $scope.scores = [];
        $scope.page = 0;
        $scope.loadAll = function() {
            Score.query({page: $scope.page, size: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                for (var i = 0; i < result.length; i++) {
                    $scope.scores.push(result[i]);
                }
            });
        };
        $scope.reset = function() {
            $scope.page = 0;
            $scope.scores = [];
            $scope.loadAll();
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.delete = function (id) {
            Score.get({id: id}, function(result) {
                $scope.score = result;
                $('#deleteScoreConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Score.delete({id: id},
                function () {
                    $scope.reset();
                    $('#deleteScoreConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.reset();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.score = {value: null, id: null};
        };
    });
