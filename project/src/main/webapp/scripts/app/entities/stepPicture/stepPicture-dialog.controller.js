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
}]);
