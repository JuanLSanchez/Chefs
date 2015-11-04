'use strict';

angular.module('chefsApp')
    .controller('RequestDetailController', function ($scope, $rootScope, $stateParams, entity, Request, User) {
        $scope.request = entity;
        $scope.load = function (id) {
            Request.get({id: id}, function(result) {
                $scope.request = result;
            });
        };
        $rootScope.$on('chefsApp:requestUpdate', function(event, result) {
            $scope.request = result;
        });
    });
