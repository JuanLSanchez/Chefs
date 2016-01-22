'use strict';

angular.module('chefsApp')
    .controller('HomeUserController', function ($scope, $stateParams, Principal, Auth, Language, $translate) {
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

//Pictures tools
        $scope.setProfilePicture = function ($file) {
            if ($file && $file.$error == 'pattern') {
                return;
            }
            if ($file) {
                var fileReader = new FileReader();
                fileReader.readAsDataURL($file);
                fileReader.onload = function (e) {
                    var data = e.target.result;
                    var base64Data = data.substr(data.indexOf('base64,') + 'base64,'.length);
                    $scope.$apply(function() {
                        $scope.user.profilePicture = base64Data;
                    });
                };
            }
        };
        $scope.setBackgroundPicture = function ($file) {
            if ($file && $file.$error == 'pattern') {
                return;
            }
            if ($file) {
                var fileReader = new FileReader();
                fileReader.readAsDataURL($file);
                fileReader.onload = function (e) {
                    var data = e.target.result;
                    var base64Data = data.substr(data.indexOf('base64,') + 'base64,'.length);
                    $scope.$apply(function() {
                        $scope.user.backgroundPicture = base64Data;
                    });
                };
            }
        };
    });
