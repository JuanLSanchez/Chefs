'use strict';

angular.module('chefsApp')
    .factory('Account', function Account($resource) {
        return $resource('api/account', {}, {
            'get': { method: 'GET', params: {}, isArray: false,
                interceptor: {
                    response: function(response) {
                        // expose response
                        if( response.data.authorities.length == 0 ||
                            !(response.data.authorities.indexOf("ROLE_ADMIN")>-1 ||
                            response.data.authorities.indexOf("ROLE_USER")>-1)){
                            response=null;
                        }
                        return response;
                    }
                }
            }
        });
    });
