'use strict';

angular.module('chefsApp')
    .factory('SocialEntity', function ($resource, DateUtils) {
        return $resource('api/socialEntitys/:id', {}, {
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
