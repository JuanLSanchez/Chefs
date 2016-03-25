'use strict';

angular.module('chefsApp').controller('RecipeDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', '$q', 'entity', 'Recipe', 'Competition', 'Vote', 'User', 'Menu', 'Event', 'SocialEntity', 'Step',
        function($scope, $stateParams, $uibModalInstance, $q, entity, Recipe, Competition, Vote, User, Menu, Event, SocialEntity, Step) {

        $scope.recipe = entity;
        $scope.competitions = Competition.query();
        $scope.votes = Vote.query();
        $scope.users = User.query();
        $scope.menus = Menu.query();
        $scope.recipes = Recipe.query();
        $scope.events = Event.query();
        $scope.socialentitys = SocialEntity.query({filter: 'recipe-is-null'});
        $q.all([$scope.recipe.$promise, $scope.socialentitys.$promise]).then(function() {
            if (!$scope.recipe.socialEntity.id) {
                return $q.reject();
            }
            return SocialEntity.get({id : $scope.recipe.socialEntity.id}).$promise;
        }).then(function(socialEntity) {
            $scope.socialentitys.push(socialEntity);
        });
        $scope.steps = Step.query();
        $scope.load = function(id) {
            Recipe.get({id : id}, function(result) {
                $scope.recipe = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('chefsApp:recipeUpdate', result);
            $uibModalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.recipe.id != null) {
                Recipe.update($scope.recipe, onSaveFinished);
            } else {
                Recipe.save($scope.recipe, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
}]);
