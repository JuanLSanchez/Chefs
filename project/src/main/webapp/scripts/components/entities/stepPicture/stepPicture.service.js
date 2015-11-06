'use strict';

angular.module('chefsApp')
    .factory('StepPicture', function ($resource, DateUtils) {
        return $resource('api/stepPictures/:id', {}, {
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
