'use strict';

angular.module('chefsApp')
    .controller('HomeUserController', function ($scope, Principal, Auth, Language, $translate) {
        $scope.success = null;
        $scope.error = null;
        Principal.identity(true).then(function(account) {
            $scope.userAccount = account;
        });

        $scope.save = function () {
            Auth.updateAccount($scope.userAccount).then(function() {
                $scope.error = null;
                $scope.success = 'OK';
                Principal.identity().then(function(account) {
                    $scope.userAccount = account;
                });
                Language.getCurrent().then(function(current) {
                    if ($scope.userAccount.langKey !== current) {
                        $translate.use($scope.userAccount.langKey);
                    }
                });
            }).catch(function() {
                $scope.success = null;
                $scope.error = 'ERROR';
            });
        };
    });
