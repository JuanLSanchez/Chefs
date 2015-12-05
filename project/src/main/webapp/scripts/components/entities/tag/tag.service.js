'use strict';

angular.module('chefsApp')
    .factory('Tag', function ($resource, DateUtils) {
        return $resource('api/tags/:id', {}, {
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
    })
    .factory('TagByNameContains', function ($resource, DateUtils) {
        return $resource('api/tags/byNameContains/:name', {}, {
            'query': { method: 'GET', isArray: true}
        });
    });
