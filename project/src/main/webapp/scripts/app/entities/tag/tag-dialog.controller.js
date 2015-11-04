'use strict';

angular.module('chefsApp').controller('TagDialogController',
    ['$scope', '$stateParams', '$modalInstance', 'entity', 'Tag', 'SocialEntity',
        function($scope, $stateParams, $modalInstance, entity, Tag, SocialEntity) {

        $scope.tag = entity;
        $scope.socialentitys = SocialEntity.query();
        $scope.load = function(id) {
            Tag.get({id : id}, function(result) {
                $scope.tag = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('chefsApp:tagUpdate', result);
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.tag.id != null) {
                Tag.update($scope.tag, onSaveFinished);
            } else {
                Tag.save($scope.tag, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
}]);
