'use strict';

angular.module('chefsApp')
    .controller('VoteDetailController', function ($scope, $rootScope, $stateParams, entity, Vote, Recipe, User, Score) {
        $scope.vote = entity;
        $scope.load = function (id) {
            Vote.get({id: id}, function(result) {
                $scope.vote = result;
            });
        };
        $rootScope.$on('chefsApp:voteUpdate', function(event, result) {
            $scope.vote = result;
        });
    });
