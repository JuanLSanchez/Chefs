'use strict';

angular.module('chefsApp')
    .factory('Competition', function ($resource, DateUtils) {
        return $resource('api/competitions/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.deadline = DateUtils.convertLocaleDateFromServer(data.deadline);
                    data.inscriptionTime = DateUtils.convertLocaleDateFromServer(data.inscriptionTime);
                    data.creationDate = DateUtils.convertLocaleDateFromServer(data.creationDate);
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    data.deadline = DateUtils.convertLocaleDateToServer(data.deadline);
                    data.inscriptionTime = DateUtils.convertLocaleDateToServer(data.inscriptionTime);
                    data.creationDate = DateUtils.convertLocaleDateToServer(data.creationDate);
                    return angular.toJson(data);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    data.deadline = DateUtils.convertLocaleDateToServer(data.deadline);
                    data.inscriptionTime = DateUtils.convertLocaleDateToServer(data.inscriptionTime);
                    data.creationDate = DateUtils.convertLocaleDateToServer(data.creationDate);
                    return angular.toJson(data);
                }
            }
        });
    });
