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
            var type = $scope.search.substr(0,1);
            var canSearch = $scope.search.length>2;
            if( type == '@' && canSearch){
                Search.users($scope.search.substr(1),{page: $scope.page, size: $scope.pageSize}).then(function(result){
                    $scope.searchResult = result;
                });
            }else if( type == '&' && canSearch){
                Search.recipes($scope.search.substr(1),{page: $scope.page, size: $scope.pageSize}).then(function(result){
                    $scope.searchResult = result;
                });
            }
        };

        $scope.goSearch = function (search){
            if (search.type == 'user'){
                $state.go('ChefRecipes', {login: search.login});
                $scope.search='@'+search.firstField;
            }else if(search.type == 'recipe'){
                $state.go('ChefRecipeDisplay', {login: search.login, id: search.firstField});
                $scope.search='&'+search.secondField;
            }
        };

    });
