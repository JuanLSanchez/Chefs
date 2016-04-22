'use strict';

angular.module('chefsApp')
    .controller('AdvancedSearchesController', function ($scope, Search, $state, ParseLinks) {
        $scope.search=null;
        $scope.searchResult=[];
        $scope.page=0;
        $scope.pageSize=10;

        $scope.save = function () {
            var diff = $scope.search.substr(0,1);
            var q = $scope.search.substr(1);
            if($scope.searchResult.length==1){
                $scope.goSearch($scope.searchResult[0]);
            }else if ( diff == '@' ){
                $state.go('listUser', {q : q});
            }else if ( diff == '&'){
                $state.go('listRecipes', {q : q});
            }else{
                $state.go('search', {q : diff+q});
            }
        };

        $scope.prev = function () {
            $scope.searchResult.clear;
            var type = $scope.search.substr(0,1);
            var canSearch = $scope.search!=null && $scope.search.length>=2;
            if( type == '@' && canSearch){
                Search.users($scope.search.substr(1),{page: $scope.page, size: $scope.pageSize}).then(function(result){
                    $scope.searchResult = result;
                });
            }else if( type == '&' && canSearch){
                Search.recipes($scope.search.substr(1),{page: $scope.page, size: $scope.pageSize}).then(function(result){
                    $scope.searchResult = result;
                });
            }else if(type == '#' && canSearch){
                Search.searchTags($scope.search.substr(1),{page: $scope.page, size: $scope.pageSize}).then(function(tags){
                    $scope.searchResult = tags.data;
                });
            }else if(canSearch){
                $scope.searchResult = [];
                var nRecipes, nUsers, nTags, totalSum, totalShow=10;
                Search.searchRecipes($scope.search,{page: $scope.page, size: $scope.pageSize}).then(function(recipes){
                    var recipeLinks = ParseLinks.parse(recipes.headers('link'));

                    Search.searchUsers($scope.search,{page: $scope.page, size: $scope.pageSize}).then(function(users){
                        var usersLinks = ParseLinks.parse(users.headers('link'));

                        Search.searchTags($scope.search,{page: $scope.page, size: $scope.pageSize}).then(function(tags){
                            var tagsLinks = ParseLinks.parse(tags.headers('link'));
                            totalSum = recipeLinks.last + usersLinks.last + tagsLinks.last + 3;

                            nRecipes = Math.floor(totalShow * (1+recipeLinks.last) / totalSum);
                            nUsers = Math.floor(totalShow * (1+usersLinks.last) / totalSum);
                            nTags = Math.floor(totalShow * (1+tagsLinks.last) / totalSum);

                            var diff =totalShow - (nRecipes+nUsers+nTags);
                            if(diff > 0){
                                if(nRecipes+diff<recipes.data.length){
                                    nRecipes = nRecipes+diff;
                                }else{
                                    nRecipes = recipes.data.length;
                                }
                            }
                            diff =totalShow - (nRecipes+nUsers+nTags);
                            if(diff > 0){
                                if(nUsers+diff<users.data.length){
                                    nUsers = nUsers+diff;
                                }else{
                                    nUsers = users.data.length;
                                }
                            }
                            diff =totalShow - (nRecipes+nUsers+nTags);
                            if(diff > 0){
                                if(nTags+diff<tags.data.length){
                                    nTags = nTags+diff;
                                }else{
                                    nTags = tags.data.length;
                                }
                            }

                            for(var i = 0; i<nRecipes && i<recipes.data.length; i++){
                                $scope.searchResult.push(recipes.data[i]);
                            }
                            if( recipeLinks.next || recipes.data.length>nRecipes ){
                                $scope.searchResult.push({type:"search", text:"global.search.recipes",
                                    typeSearch:"recipe", q:$scope.search});
                            }
                            for(var i = 0; i<nUsers && i<users.data.length; i++){
                                $scope.searchResult.push(users.data[i]);
                            }
                            if( usersLinks.next || users.data.length>nUsers){
                                $scope.searchResult.push({type:"search", text:"global.search.users",
                                    typeSearch:"user", q:$scope.search});

                            }
                            for(var i = 0; i<nTags && i<tags.data.length; i++){
                                $scope.searchResult.push(tags.data[i]);
                            }
                        });
                    });
                });
            }
        };

        $scope.goSearch = function (search){
            if (search.type == 'user'){
                $state.go('ChefRecipes', {login: search.login});
                $scope.search='@'+search.firstField;
            }else if(search.type == 'recipe') {
                $state.go('ChefRecipeDisplay', {login: search.login, id: search.firstField});
                $scope.search = '&' + search.secondField;
            }else if(search.type == 'tag'){
                $state.go('listRecipesByTag', {tag:search.firstField});
                $scope.search = '#' + search.secondField;
            }else if(search.type == 'search'){
                var q = search.q;
                if(search.typeSearch=="recipe"){
                    $state.go('listRecipes', {q : q});
                    $scope.search='&';
                }else if(search.typeSearch=="user"){
                    $state.go('listUser', {q : q});
                    $scope.search='@';
                }
                $scope.search+=q;
            }
        };

        $scope.cut = function(string){
            return string;
        };

        $scope.simbolByType = function(search){
            var result = '&';
            if(search.type=='tag'){
                result = '#';
            }else if(search.type=='user'){
                result = '@';
            }
            return result;
        }

    });
