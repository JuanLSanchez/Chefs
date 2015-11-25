'use strict';

angular.module('chefsApp').controller('StepPictureDialogController',
    ['$scope', '$stateParams', '$modalInstance', 'entity', 'StepPicture', 'Step',
        function($scope, $stateParams, $modalInstance, entity, StepPicture, Step) {

        $scope.stepPicture = entity;
        $scope.steps = Step.query();
        $scope.load = function(id) {
            StepPicture.get({id : id}, function(result) {
                $scope.stepPicture = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('chefsApp:stepPictureUpdate', result);
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.stepPicture.id != null) {
                StepPicture.update($scope.stepPicture, onSaveFinished);
            } else {
                StepPicture.save($scope.stepPicture, onSaveFinished);
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

        $scope.setSrc = function ($file, stepPicture) {
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
                        stepPicture.src = base64Data;
                    });
                };
            }
        };
}]);
