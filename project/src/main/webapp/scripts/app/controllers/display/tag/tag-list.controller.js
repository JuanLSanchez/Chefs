'use strict';

angular.module('chefsApp')
    .controller('TagListController', function ($scope, $state, Search, $stateParams) {
        $scope.tags = [];
        $scope.page = 0;
        $scope.pageSize = 20;

        $scope.loadAll = function() {
            var q = $stateParams.q;
            Search.searchTags(q, {page: $scope.page, size: $scope.pageSize}).then(function(tags){
                $scope.tags = tags.data.map(function(tag){ return {id:tag.firstField, name:tag.secondField };});
            });
        };

        $scope.loadAll();
    });
