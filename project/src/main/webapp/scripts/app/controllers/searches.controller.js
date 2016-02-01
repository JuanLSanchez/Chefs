'use strict';

angular.module('chefsApp')
    .controller('SearchesController', function ($scope, Search, $state) {
        $scope.search=null;
        $scope.searchResult=[];
        $scope.page=0;
        $scope.pageSize=5;

        $scope.save = function () {
            var diff = $scope.search.substr(0,1);
            var q = $scope.search.substr(1);
            if ( diff == '@' ){
                $state.go('listUser', {q : q});
            }
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
