'use strict';

angular.module('chefsApp')
    .controller('SearchesController', function ($scope, Search) {
        $scope.search=null;
        $scope.searchResult=[];
        $scope.page=0;
        $scope.pageSize=5;

        $scope.save = function () {

        };

        $scope.prev = function () {
            $scope.searchResult.clear;
            if($scope.search.substr(0,1) == '@' && $scope.search.length>2){
                Search.users($scope.search.substr(1),{page: $scope.page, size: $scope.pageSize}).then(function(result){
                    $scope.searchResult = result;
                });
            }
        };

    });
