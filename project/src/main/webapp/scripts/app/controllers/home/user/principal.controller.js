'use strict';

angular.module('chefsApp')
    .controller('PrincipalUserController', function ($scope, Principal, Auth, Language, $translate) {
        $scope.backgroundStyle = {};
        $scope.thumbailStyle = {'background-color': '#f5f5f5'}
        $scope.user = null;

//Get principal
        Principal.identity(true).then(function(account) {
            $scope.user = account;
        });

    });
