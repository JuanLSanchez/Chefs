'use strict';

angular.module('chefsApp').controller('StepDialogController',
    ['$scope', '$stateParams', '$modalInstance', 'entity', 'Step', 'Recipe', 'StepPicture', 'Ingredient',
        function($scope, $stateParams, $modalInstance, entity, Step, Recipe, StepPicture, Ingredient) {

        $scope.step = entity;
        $scope.recipes = Recipe.query();
        $scope.steppictures = StepPicture.query();
        $scope.ingredients = Ingredient.query();
        $scope.load = function(id) {
            Step.get({id : id}, function(result) {
                $scope.step = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('chefsApp:stepUpdate', result);
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.step.id != null) {
                Step.update($scope.step, onSaveFinished);
            } else {
                Step.save($scope.step, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
}]);
