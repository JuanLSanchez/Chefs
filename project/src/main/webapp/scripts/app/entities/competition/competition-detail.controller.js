'use strict';

angular.module('chefsApp')
    .controller('CompetitionDetailController', function ($scope, $rootScope, $stateParams, entity, Competition, Recipe, Opinion, User, SocialEntity) {
        $scope.competition = entity;
        $scope.load = function (id) {
            Competition.get({id: id}, function(result) {
                $scope.competition = result;
            });
        };
        $rootScope.$on('chefsApp:competitionUpdate', function(event, result) {
            $scope.competition = result;
        });
    });
