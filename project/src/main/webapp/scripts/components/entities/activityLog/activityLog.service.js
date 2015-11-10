'use strict';

angular.module('chefsApp')
    .factory('ActivityLog', function ($resource, DateUtils) {
        return $resource('api/activityLogs/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.moment = DateUtils.convertDateTimeFromServer(data.moment);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    });
