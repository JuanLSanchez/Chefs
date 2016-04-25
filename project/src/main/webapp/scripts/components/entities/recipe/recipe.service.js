'use strict';

angular.module('chefsApp')
    .factory('Recipe', function ($resource, DateUtils) {
        return $resource('api/recipes/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.creationDate = DateUtils.convertDateTimeFromServer(data.creationDate);
                    data.updateDate = DateUtils.convertDateTimeFromServer(data.updateDate);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    });

angular.module('chefsApp')
    .factory('RecipeUser', function ($resource) {
        return $resource('api/recipes/user/:login', {}, {
            'query': { method: 'GET', isArray: true},
            'get': { method: 'GET', isArray: true}
        });
    });
angular.module('chefsApp')
    .factory('RecipeUserDTO', function ($resource) {
        return $resource('api/recipes_dto/user/:login', {}, {
            'query': { method: 'GET', isArray: true},
            'get': { method: 'GET', isArray: true}
        });
    });
angular.module('chefsApp')
    .factory('RecipeSearch', function ($http) {
        return {
            recipeLikes: function(q,params){
                return $http.get('api/recipes_dto/likes', {params:params});
            },
            recipeAssessed: function(q,params){
                return $http.get('api/recipes_dto/assessed', {params:params});
            }
        };
    });
