'use strict';

angular.module('chefsApp')
    .controller('HomeUserController', function ($scope, Principal, Auth, Language, $translate) {
        $scope.success = null;
        $scope.error = null;
        $scope.editMode = false;
        $scope.backgroundStyle = {};
        $scope.errorEmailExists = null;
        $scope.imagesModified = false;
        $scope.thumbailStyle = {'background-color': '#f5f5f5'}
        $scope.user = null;

//Check images
        $scope.modifiedImage = function(){
            $scope.imagesModified = true;
        };

//Get principal
        Principal.identity(true).then(function(account) {
            $scope.user = account;
        });

//Save function
        $scope.save = function () {
            Auth.updateAccount($scope.user).then(function() {
                $scope.error = null;
                $scope.errorEmailExists = null;
                $scope.success = 'OK';
                Language.getCurrent().then(function(current) {
                    if ($scope.user.langKey !== current) {
                        $translate.use($scope.user.langKey);
                    }
                });
                $scope.editMode = false;
            }).catch(function(result) {
                $scope.success = null;
                if (result.status == 400){
//When the server return http status 400, it is that the email exist
                    $scope.errorEmailExists = 'DUPLICATE';
                }else{
                    $scope.error = 'ERROR';
                }
            });
            $scope.userForm.$setPristine();
            $scope.imagesModified = false;
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
// Switch views edit and show
        $scope.switchMode = function(){
            $scope.editMode = !$scope.editMode;
        };
// Redirect followers and following
        $scope.redirect = function(){
            return 'home';
        }

    });
