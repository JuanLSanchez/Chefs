'use strict';

angular.module('chefsApp')
    .factory('Like', function ($http) {
        return {
            likesOfSocialEntity:function(socialEntityId){
                return $http.get('api/likes/'+socialEntityId);
            },
            likesOfSocialEntityByUser:function(socialEntityId){
                return $http.get('api/likes/user/'+socialEntityId);
            },
            update: function(socialEntityId){
                return $http.put('api/likes/'+socialEntityId);
            }
        };
    });
