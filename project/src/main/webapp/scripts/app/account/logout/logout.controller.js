'use strict';

angular.module('chefsApp')
    .controller('LogoutController', function (Auth) {
        Auth.logout();
    });
