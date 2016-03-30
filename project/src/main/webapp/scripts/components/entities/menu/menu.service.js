'use strict';

angular.module('chefsApp')
    .factory('Menu', function ($http, DateUtils) {
        return {
            findAllByScheduleId: function(scheduleId){
                return $http({
                    url : 'api/menus/'+scheduleId,
                    method : 'GET',
                    transformResponse: function (data) {
                        data = angular.fromJson(data);
                        for(var i in data){
                            data[i].time = DateUtils.convertDateTimeFromServer(data[i].time);
                        }
                        return data;
                    }
                });
            },
            save: function(scheduleId, data){
                return $http({
                    url : 'api/menus/'+scheduleId,
                    method : 'POST',
                    transformResponse: function (data) {
                        data = angular.fromJson(data);
                        data.time = DateUtils.convertDateTimeFromServer(data.time);
                        return data;
                    },
                    data : data
                });
            },
            update: function(scheduleId, data){
                return $http({
                    url : 'api/menus/'+scheduleId,
                    method : 'PUT',
                    transformResponse: function (data) {
                        data = angular.fromJson(data);
                        data.time = DateUtils.convertDateTimeFromServer(data.time);
                        return data;
                    },
                    data : data
                });
            },
            delete: function(menuId){
                return $http.delete('api/menus/'+menuId);
            },
            addRecipe: function(menuId, recipeId){
                return $http({
                    url : 'api/menus/addRecipe/'+menuId+'/'+recipeId,
                    method : 'PUT',
                    transformResponse: function (data) {
                        data = angular.fromJson(data);
                        data.time = DateUtils.convertDateTimeFromServer(data.time);
                        return data;
                    }
                });
            },
            removeRecipe: function(menuId, recipeId){
                return $http({
                    url : 'api/menus/removeRecipe/'+menuId+'/'+recipeId,
                    method : 'PUT',
                    transformResponse: function (data) {
                        data = angular.fromJson(data);
                        data.time = DateUtils.convertDateTimeFromServer(data.time);
                        return data;
                    }
                });
            }
        };
    });
