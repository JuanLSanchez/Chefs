'use strict';

angular.module('chefsApp')
    .controller('ChefUserController', function ($scope, entity) {
        $scope.backgroundStyle = {};
        $scope.thumbailStyle = {'background-color': '#f5f5f5'};
        $scope.user = entity;

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
                        $scope.modifiedImage();
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
                        $scope.modifiedImage();
                    });
                };
            }
        };

    });
