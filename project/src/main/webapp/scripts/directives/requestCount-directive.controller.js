'use strict';

angular.module('chefsApp')
    .controller('RequestCountDirectiveController', function ($scope, RequestAPI) {
        $scope.requestInfo=null;

        $scope.loadAll = function() {
            RequestAPI.requestInfo($scope.user)
                .then(function(result){
                    $scope.requestInfo=result.data;
                });
        };

        $scope.reset = function() {
            $scope.requestInfo=null;
            $scope.loadAll();
        };

        $scope.loadAll();

        $scope.refresh = function () {
            $scope.reset();
            $scope.clear();
        };
    });
