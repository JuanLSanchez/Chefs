'use strict';

angular.module('chefsApp').controller('RequestDialogController',
    ['$scope', '$stateParams', '$modalInstance', 'entity', 'Request', 'User',
        function($scope, $stateParams, $modalInstance, entity, Request, User) {

        $scope.request = entity;
        $scope.users = User.query();
        $scope.load = function(id) {
            Request.get({id : id}, function(result) {
                $scope.request = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('chefsApp:requestUpdate', result);
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.request.id != null) {
                Request.update($scope.request, onSaveFinished);
            } else {
                Request.save($scope.request, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
}]);
