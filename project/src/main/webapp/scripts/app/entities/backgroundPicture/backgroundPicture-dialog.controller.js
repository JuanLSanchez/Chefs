'use strict';

angular.module('chefsApp').controller('BackgroundPictureDialogController',
    ['$scope', '$stateParams', '$modalInstance', '$q', 'entity', 'BackgroundPicture', 'User',
        function($scope, $stateParams, $modalInstance, $q, entity, BackgroundPicture, User) {

        $scope.backgroundPicture = entity;
        $scope.users = User.query();
        $scope.load = function(id) {
            BackgroundPicture.get({id : id}, function(result) {
                $scope.backgroundPicture = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('chefsApp:backgroundPictureUpdate', result);
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.backgroundPicture.id != null) {
                BackgroundPicture.update($scope.backgroundPicture, onSaveFinished);
            } else {
                BackgroundPicture.save($scope.backgroundPicture, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };

        $scope.abbreviate = function (text) {
            if (!angular.isString(text)) {
                return '';
            }
            if (text.length < 30) {
                return text;
            }
            return text ? (text.substring(0, 15) + '...' + text.slice(-10)) : '';
        };

        $scope.byteSize = function (base64String) {
            if (!angular.isString(base64String)) {
                return '';
            }
            function endsWith(suffix, str) {
                return str.indexOf(suffix, str.length - suffix.length) !== -1;
            }
            function paddingSize(base64String) {
                if (endsWith('==', base64String)) {
                    return 2;
                }
                if (endsWith('=', base64String)) {
                    return 1;
                }
                return 0;
            }
            function size(base64String) {
                return base64String.length / 4 * 3 - paddingSize(base64String);
            }
            function formatAsBytes(size) {
                return size.toString().replace(/\B(?=(\d{3})+(?!\d))/g, " ") + " bytes";
            }

            return formatAsBytes(size(base64String));
        };

        $scope.setSrc = function ($file, backgroundPicture) {
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
                        backgroundPicture.src = base64Data;
                    });
                };
            }
        };
}]);
