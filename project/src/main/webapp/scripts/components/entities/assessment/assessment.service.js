'use strict';

angular.module('chefsApp')
    .factory('Assessment', function ($resource, $http) {
        return {
            assessmentOfSocialEntity:function(socialEntityId){
                return $http({
                    url : 'api/assessments/'+socialEntityId,
                    method : 'GET'
                });
            },
            assessmentOfSocialEntityByUser:function(socialEntityId){
                return $http.get('api/assessments/user/'+socialEntityId);
            },
            update: function(socialEntityId, data){
                return $http({
                    url : 'api/assessments/'+socialEntityId,
                    method : 'PUT',
                    data : data
                });
            },
            delete: function(socialEntityId){
                return $http.delete('api/assessments/user/'+socialEntityId);
            }
        };
    });

