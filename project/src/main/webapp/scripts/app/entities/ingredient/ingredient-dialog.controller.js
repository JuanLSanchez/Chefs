'use strict';

angular.module('chefsApp').controller('IngredientDialogController',
    ['$scope', '$stateParams', '$modalInstance', 'entity', 'Ingredient', 'Step', 'Food',
        function($scope, $stateParams, $modalInstance, entity, Ingredient, Step, Food) {

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
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.ingredient.id != null) {
                Ingredient.update($scope.ingredient, onSaveFinished);
            } else {
                Ingredient.save($scope.ingredient, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
}]);
