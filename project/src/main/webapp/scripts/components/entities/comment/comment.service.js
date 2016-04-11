'use strict';

angular.module('chefsApp')
    .factory('Comment', function ($resource, DateUtils, $http) {
        return {
            findAllOfSE:function(socialEntityId,params){
                return $http({
                    url : 'api/comments/'+socialEntityId,
                    method : 'GET',
                    params: params,
                    transformResponse: function (data) {
                        data = angular.fromJson(data);
                        for(var i in data){
                            data[i].creationMoment = DateUtils.convertDateTimeFromServer(data[i].creationMoment);
                        }
                        return data;
                    }
                });
            },
            save: function(recipeId, data){
                return $http({
                    url : 'api/comments/'+recipeId,
                    method : 'POST',
                    transformResponse: function (data) {
                        data = angular.fromJson(data);
                        data.creationMoment = DateUtils.convertDateTimeFromServer(data.creationMoment);
                        return data;
                    },
                    data : data
                });
            },
            update: function(commentId, data){
                return $http({
                    url : 'api/comments/'+commentId,
                    method : 'PUT',
                    transformResponse: function (data) {
                        data = angular.fromJson(data);
                        data.creationMoment = DateUtils.convertDateTimeFromServer(data.creationMoment);
                        return data;
                    },
                    data : data
                });
            },
            delete: function(commentId){
                return $http.delete('api/comments/'+commentId);
            }
        };
    });
