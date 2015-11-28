'use strict';

angular.module('chefsApp')
    .factory('Food', function ($resource, DateUtils) {
        return $resource('api/foods/:id', {}, {
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
angular.module('chefsApp')
    .factory('FoodSearch', function ($resource, DateUtils) {
        return $resource('api/foods/search/:name', {}, {
            'query': { method: 'GET', isArray: true}
        });
    });
