'use strict';

angular.module('chefsApp')
    .controller('HomeUserController', function ($scope, Principal, Auth, Language, $translate) {
        $scope.success = null;
        $scope.error = null;
        Principal.identity(true).then(function(account) {
            $scope.user = account;
        });

        $scope.save = function () {
            Auth.updateAccount($scope.user).then(function() {
                $scope.error = null;
                $scope.success = 'OK';
                Principal.identity().then(function(account) {
                    $scope.user = account;
                });
                Language.getCurrent().then(function(current) {
                    if ($scope.user.langKey !== current) {
                        $translate.use($scope.userAccount.langKey);
                    }
                });
            }).catch(function() {
                $scope.success = null;
                $scope.error = 'ERROR';
            });
        };
    });
