'use strict';

angular.module('chefsApp').controller('ActivityLogDialogController',
    ['$scope', '$stateParams', '$modalInstance', 'entity', 'ActivityLog',
        function($scope, $stateParams, $modalInstance, entity, ActivityLog) {

        $scope.activityLog = entity;
        $scope.load = function(id) {
            ActivityLog.get({id : id}, function(result) {
                $scope.activityLog = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('chefsApp:activityLogUpdate', result);
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.activityLog.id != null) {
                ActivityLog.update($scope.activityLog, onSaveFinished);
            } else {
                ActivityLog.save($scope.activityLog, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
}]);
