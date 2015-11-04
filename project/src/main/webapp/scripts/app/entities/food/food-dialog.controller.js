'use strict';

angular.module('chefsApp').controller('FoodDialogController',
    ['$scope', '$stateParams', '$modalInstance', 'entity', 'Food', 'Ingredient',
        function($scope, $stateParams, $modalInstance, entity, Food, Ingredient) {

        $scope.food = entity;
        $scope.ingredients = Ingredient.query();
        $scope.load = function(id) {
            Food.get({id : id}, function(result) {
                $scope.food = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('chefsApp:foodUpdate', result);
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.food.id != null) {
                Food.update($scope.food, onSaveFinished);
            } else {
                Food.save($scope.food, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
}]);
