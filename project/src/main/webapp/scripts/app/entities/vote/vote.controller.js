'use strict';

angular.module('chefsApp')
    .controller('VoteController', function ($scope, Vote, ParseLinks) {
        $scope.votes = [];
        $scope.page = 0;
        $scope.loadAll = function() {
            Vote.query({page: $scope.page, size: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                for (var i = 0; i < result.length; i++) {
                    $scope.votes.push(result[i]);
                }
            });
        };
        $scope.reset = function() {
            $scope.page = 0;
            $scope.votes = [];
            $scope.loadAll();
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.delete = function (id) {
            Vote.get({id: id}, function(result) {
                $scope.vote = result;
                $('#deleteVoteConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Vote.delete({id: id},
                function () {
                    $scope.reset();
                    $('#deleteVoteConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.reset();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.vote = {isFinal: null, abstain: null, comment: null, completedScore: null, id: null};
        };
    });
