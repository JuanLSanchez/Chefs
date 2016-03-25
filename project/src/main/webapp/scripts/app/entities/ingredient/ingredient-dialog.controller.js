'use strict';

angular.module('chefsApp').controller('IngredientDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'Ingredient', 'Step', 'Food',
        function($scope, $stateParams, $uibModalInstance, entity, Ingredient, Step, Food) {

        $scope.ingredient = entity;
        $scope.steps = Step.query();
        $scope.foods = Food.query();
        $scope.load = function(id) {
            Ingredient.get({id : id}, function(result) {
                $scope.ingredient = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('chefsApp:ingredientUpdate', result);
            $uibModalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.ingredient.id != null) {
                Ingredient.update($scope.ingredient, onSaveFinished);
            } else {
                Ingredient.save($scope.ingredient, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
}]);
