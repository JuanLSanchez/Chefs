'use strict';

angular.module('chefsApp').controller('RecipeEditController',
        function($rootScope, $scope, $stateParams, entity, Recipe) {

        $scope.step = {position: null, section: null, id: null};
        $scope.recipe = entity;
        $scope.load = function(id) {
            Recipe.get({id : id}, function(result) {
                $scope.recipe = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('chefsApp:recipeUpdate', result);
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.recipe.id != null) {
                Recipe.update($scope.recipe, onSaveFinished);
            } else {
                Recipe.save($scope.recipe, onSaveFinished);
            }
        };

//Steps
        $scope.addStep = function (){
            $scope.recipe.steps.push(angular.copy($scope.step));
            $scope.step = {position: null, section: null, id: null};
        };
        $scope.deleteStep = function(position){
            $scope.recipe.steps.splice(position,1);
        };
        $scope.sortableOptions = {
            stop: function(e, ui) {
                $scope.recipe.steps.forEach(function(element, index, array){element.position = index;});
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
        $rootScope.securityEntity = $scope.recipe;

});
