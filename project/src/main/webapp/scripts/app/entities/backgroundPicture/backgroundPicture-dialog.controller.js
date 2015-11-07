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
}]);
