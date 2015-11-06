'use strict';

angular.module('chefsApp')
    .controller('OpinionDetailController', function ($scope, $rootScope, $stateParams, entity, Opinion, Score, Competition) {
        $scope.opinion = entity;
        $scope.load = function (id) {
            Opinion.get({id: id}, function(result) {
                $scope.opinion = result;
            });
        };
        $rootScope.$on('chefsApp:opinionUpdate', function(event, result) {
            $scope.opinion = result;
        });
    });
