'use strict';

angular.module('chefsApp')
    .factory('Assessment', function ($resource, DateUtils) {
        return $resource('api/assessments/:id', {}, {
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
