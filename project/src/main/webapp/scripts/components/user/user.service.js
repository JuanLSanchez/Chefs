'use strict';

angular.module('chefsApp')
    .factory('User', function ($resource) {
        return $resource('api/users/:login', {}, {
                'query': {method: 'GET', isArray: true},
                'get': {
                    method: 'GET',
                    transformResponse: function (data) {
                        data = angular.fromJson(data);
                        return data;
                    }
                },
                'update': { method:'PUT' }
            });
        });

angular.module('chefsApp')
    .factory('UserAPI', function ($http) {
        return {
            followers: function(followed, params){
                return $http.get('api/users/followers/'+followed, {params:params});
            },
            following: function(follower, params){
                return $http.get('api/users/following/'+follower, {params:params});
            }
        };
    });
