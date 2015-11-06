'use strict';

angular.module('chefsApp').controller('MenuDialogController',
    ['$scope', '$stateParams', '$modalInstance', 'entity', 'Menu', 'Schedule', 'Recipe',
        function($scope, $stateParams, $modalInstance, entity, Menu, Schedule, Recipe) {

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
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.menu.id != null) {
                Menu.update($scope.menu, onSaveFinished);
            } else {
                Menu.save($scope.menu, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
}]);
