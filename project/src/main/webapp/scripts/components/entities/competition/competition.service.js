'use strict';

angular.module('chefsApp')
    .factory('Competition', function ($resource, DateUtils) {
        return $resource('api/competitions/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.deadline = DateUtils.convertDateTimeFromServer(data.deadline);
                    data.inscriptionTime = DateUtils.convertDateTimeFromServer(data.inscriptionTime);
                    data.creationDate = DateUtils.convertDateTimeFromServer(data.creationDate);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    });
