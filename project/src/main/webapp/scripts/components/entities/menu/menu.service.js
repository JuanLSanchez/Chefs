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
                return $http.post('api/menus/'+scheduleId, data);
            },
            update: function(scheduleId, data){
                return $http.put('api/menus/'+scheduleId, data);
            },
            delete: function(menuId){
                return $http.delete('api/menus/'+menuId);
            }
        };
    });
