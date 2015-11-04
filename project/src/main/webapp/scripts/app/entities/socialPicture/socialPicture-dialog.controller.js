'use strict';

angular.module('chefsApp').controller('SocialPictureDialogController',
    ['$scope', '$stateParams', '$modalInstance', 'entity', 'SocialPicture', 'SocialEntity',
        function($scope, $stateParams, $modalInstance, entity, SocialPicture, SocialEntity) {

        $scope.socialPicture = entity;
        $scope.socialentitys = SocialEntity.query();
        $scope.load = function(id) {
            SocialPicture.get({id : id}, function(result) {
                $scope.socialPicture = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('chefsApp:socialPictureUpdate', result);
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.socialPicture.id != null) {
                SocialPicture.update($scope.socialPicture, onSaveFinished);
            } else {
                SocialPicture.save($scope.socialPicture, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
}]);
