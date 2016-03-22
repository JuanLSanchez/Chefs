'use strict';

angular.module('chefsApp').controller('ScheduleEditController',
        function($state, $scope, $stateParams, entity, Schedule) {

        $scope.schedule = entity;

        var onSaveFinished = function (result) {
            $state.go('HomeSchedulesDisplay', {id:result.id, message:result});
        };

        $scope.save = function () {
            if ($scope.schedule.id != null) {
                Schedule.update($scope.schedule, onSaveFinished);
            } else {
                Schedule.save($scope.schedule, onSaveFinished);
            }
        };

});
