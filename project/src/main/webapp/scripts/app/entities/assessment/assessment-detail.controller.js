'use strict';

angular.module('chefsApp')
    .controller('AssessmentDetailController', function ($scope, $rootScope, $stateParams, entity, Assessment, User, SocialEntity) {
        $scope.assessment = entity;
        $scope.load = function (id) {
            Assessment.get({id: id}, function(result) {
                $scope.assessment = result;
            });
        };
        $rootScope.$on('chefsApp:assessmentUpdate', function(event, result) {
            $scope.assessment = result;
        });
    });
