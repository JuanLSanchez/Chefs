'use strict';

angular.module('chefsApp')
    .factory('Recipe', function ($resource, DateUtils) {
        return $resource('api/recipes/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.creationDate = DateUtils.convertDateTimeFromServer(data.creationDate);
                    data.updateDate = DateUtils.convertDateTimeFromServer(data.updateDate);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    });
