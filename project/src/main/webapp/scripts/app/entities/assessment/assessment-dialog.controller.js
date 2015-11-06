'use strict';

angular.module('chefsApp').controller('AssessmentDialogController',
    ['$scope', '$stateParams', '$modalInstance', 'entity', 'Assessment', 'User', 'SocialEntity',
        function($scope, $stateParams, $modalInstance, entity, Assessment, User, SocialEntity) {

        $scope.assessment = entity;
        $scope.users = User.query();
        $scope.socialentitys = SocialEntity.query();
        $scope.load = function(id) {
            Assessment.get({id : id}, function(result) {
                $scope.assessment = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('chefsApp:assessmentUpdate', result);
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.assessment.id != null) {
                Assessment.update($scope.assessment, onSaveFinished);
            } else {
                Assessment.save($scope.assessment, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
}]);
