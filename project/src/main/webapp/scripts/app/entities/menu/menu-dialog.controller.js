'use strict';

angular.module('chefsApp').controller('MenuDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'Menu', 'Schedule', 'Recipe',
        function($scope, $stateParams, $uibModalInstance, entity, Menu, Schedule, Recipe) {

        $scope.menu = entity;
        $scope.schedules = Schedule.query();
        $scope.recipes = Recipe.query();
        $scope.load = function(id) {
            Menu.get({id : id}, function(result) {
                $scope.menu = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('chefsApp:menuUpdate', result);
            $uibModalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.menu.id != null) {
                Menu.update($scope.menu, onSaveFinished);
            } else {
                Menu.save($scope.menu, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
}]);
