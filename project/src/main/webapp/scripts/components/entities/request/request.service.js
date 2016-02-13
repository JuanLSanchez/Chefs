'use strict';

angular.module('chefsApp')
    .factory('Request', function ($resource, DateUtils) {
        return $resource('api/requests/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.creationDate = DateUtils.convertDateTimeFromServer(data.creationDate);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    });
angular.module('chefsApp')
    .factory('RequestAPI', function ($http) {
        var updateCount = {};
        return {
            findRequestWithPrincipalAsFollowerAndFollowed: function(followed, params){
                return $http.get('api/requests/follower/'+followed, params);
            },
            findRequestWithPrincipalAsFollowedAndFollower: function(follower, params){
                return $http.get('api/requests/followed/'+follower, params);
            },
            followed: function(followed, params){
                return $http.put('api/requests/follower/'+followed, params);
            },
            follower: function(follower, params){
                return $http.put('api/requests/followed/'+follower, params);
            },
            requestInfo: function(login, params){
                return $http.get('api/requests/count/'+login, params);
            },
            getUpdateCount: function(login){
                return updateCount[login];
            },
            setUpdateCount: function(login, value){
                updateCount[login]=value;
            }
        };
    });
