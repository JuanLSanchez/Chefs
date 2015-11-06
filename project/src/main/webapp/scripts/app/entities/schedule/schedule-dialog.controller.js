'use strict';

angular.module('chefsApp').controller('ScheduleDialogController',
    ['$scope', '$stateParams', '$modalInstance', 'entity', 'Schedule', 'User', 'Menu',
        function($scope, $stateParams, $modalInstance, entity, Schedule, User, Menu) {

        $scope.schedule = entity;
        $scope.users = User.query();
        $scope.menus = Menu.query();
        $scope.load = function(id) {
            Schedule.get({id : id}, function(result) {
                $scope.schedule = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('chefsApp:scheduleUpdate', result);
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.schedule.id != null) {
                Schedule.update($scope.schedule, onSaveFinished);
            } else {
                Schedule.save($scope.schedule, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
}]);
