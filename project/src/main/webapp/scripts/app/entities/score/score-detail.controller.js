'use strict';

angular.module('chefsApp')
    .controller('ScoreDetailController', function ($scope, $rootScope, $stateParams, entity, Score, Vote, Opinion) {
        $scope.score = entity;
        $scope.load = function (id) {
            Score.get({id: id}, function(result) {
                $scope.score = result;
            });
        };
        $rootScope.$on('chefsApp:scoreUpdate', function(event, result) {
            $scope.score = result;
        });
    });
