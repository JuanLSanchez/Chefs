'use strict';

angular.module('chefsApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


