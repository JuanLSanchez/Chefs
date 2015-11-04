'use strict';

angular.module('chefsApp').controller('ProfilePictureDialogController',
    ['$scope', '$stateParams', '$modalInstance', '$q', 'entity', 'ProfilePicture', 'User',
        function($scope, $stateParams, $modalInstance, $q, entity, ProfilePicture, User) {

        $scope.profilePicture = entity;
        $scope.users = User.query();
        $scope.load = function(id) {
            ProfilePicture.get({id : id}, function(result) {
                $scope.profilePicture = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('chefsApp:profilePictureUpdate', result);
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.profilePicture.id != null) {
                ProfilePicture.update($scope.profilePicture, onSaveFinished);
            } else {
                ProfilePicture.save($scope.profilePicture, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
}]);
