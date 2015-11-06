'use strict';

angular.module('chefsApp')
    .factory('Schedule', function ($resource, DateUtils) {
        return $resource('api/schedules/:id', {}, {
            'query': { method: 'GET', isArray: true},
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
