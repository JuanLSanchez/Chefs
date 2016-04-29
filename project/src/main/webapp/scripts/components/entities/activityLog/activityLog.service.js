'use strict';

angular.module('chefsApp')
    .factory('ActivityLog', function ($http, DateUtils) {
        return {
            activityLogs: function(params){
                return $http({
                    url : "api/activityLogs",
                    params : params,
                    methor : 'GET',
                    transformResponse: function (data) {
                        data = angular.fromJson(data);
                        for(var i in data){
                            data[i].moment = DateUtils.convertDateTimeFromServer(data[i].moment);
                        }
                        return data;
                    }

                });
            },
            activityLogsOfUser: function(login, params){
                return $http({
                    url : "api/activityLogs/"+login,
                    params : params,
                    methor : 'GET',
                    transformResponse: function (data) {
                        data = angular.fromJson(data);
                        for(var i in data){
                            data[i].moment = DateUtils.convertDateTimeFromServer(data[i].moment);
                        }
                        return data;
                    }

                });
            }
        };
    });
