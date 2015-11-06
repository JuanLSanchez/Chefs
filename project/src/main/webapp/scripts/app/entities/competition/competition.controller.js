'use strict';

angular.module('chefsApp')
    .controller('CompetitionController', function ($scope, Competition, ParseLinks) {
        $scope.competitions = [];
        $scope.page = 0;
        $scope.loadAll = function() {
            Competition.query({page: $scope.page, size: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                for (var i = 0; i < result.length; i++) {
                    $scope.competitions.push(result[i]);
                }
            });
        };
        $scope.reset = function() {
            $scope.page = 0;
            $scope.competitions = [];
            $scope.loadAll();
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.delete = function (id) {
            Competition.get({id: id}, function(result) {
                $scope.competition = result;
                $('#deleteCompetitionConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Competition.delete({id: id},
                function () {
                    $scope.reset();
                    $('#deleteCompetitionConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.reset();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.competition = {name: null, description: null, deadline: null, rules: null, inscriptionTime: null, maxNRecipesByChefs: null, creationDate: null, completedScore: null, publicJury: null, id: null};
        };
    });
