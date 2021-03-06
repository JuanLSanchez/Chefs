'use strict';

angular.module('chefsApp')
    .factory('SocialPicture', function ($resource, DateUtils) {
        return $resource('api/socialPictures/:id', {}, {
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
