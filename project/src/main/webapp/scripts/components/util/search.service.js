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
            }
        };
    });
