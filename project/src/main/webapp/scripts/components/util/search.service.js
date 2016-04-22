'use strict';

angular.module('chefsApp')
    .factory('Search', function ($http) {
        return {
            users: function(q, params){
                return $http.get('api/search/users/'+q, {params:params}).then(function(response) {
                    return response.data;
                });
            },
            usersList: function(q, params){
                return $http.get('api/users/likeLoginOrLikeFirstName/'+q, {params:params});
            },
            recipes: function(q, params){
                return $http.get('api/search/recipes/'+q, {params:params}).then(function(response) {
                    return response.data;
                });
            },
            recipesList: function(q, params){
                return $http.get('api/recipes_dto/findAllIsVisibilityAndLikeName/'+q, {params:params});
            },
            recipesListByTag: function(tag, params){
                return $http.get('api/recipes_dto/withTag/'+tag, {params:params});
            },
            searchTags: function(q, params){
                return $http.get('api/search/tags/'+q, {params:params});
            },
            searchUsers: function(q, params){
                return $http.get('api/search/users/'+q, {params:params});
            },
            searchRecipes: function(q, params){
                return $http.get('api/search/recipes/'+q, {params:params});
            },
        };
    });
