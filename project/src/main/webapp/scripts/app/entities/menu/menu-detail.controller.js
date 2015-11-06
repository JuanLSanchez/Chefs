'use strict';

angular.module('chefsApp')
    .controller('MenuDetailController', function ($scope, $rootScope, $stateParams, entity, Menu, Schedule, Recipe) {
        $scope.menu = entity;
        $scope.load = function (id) {
            Menu.get({id: id}, function(result) {
                $scope.menu = result;
            });
        };
        $rootScope.$on('chefsApp:menuUpdate', function(event, result) {
            $scope.menu = result;
        });
    });
