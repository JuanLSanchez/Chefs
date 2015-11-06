'use strict';

angular.module('chefsApp').controller('VoteDialogController',
    ['$scope', '$stateParams', '$modalInstance', 'entity', 'Vote', 'Recipe', 'User', 'Score',
        function($scope, $stateParams, $modalInstance, entity, Vote, Recipe, User, Score) {

        $scope.vote = entity;
        $scope.recipes = Recipe.query();
        $scope.users = User.query();
        $scope.scores = Score.query();
        $scope.load = function(id) {
            Vote.get({id : id}, function(result) {
                $scope.vote = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('chefsApp:voteUpdate', result);
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.vote.id != null) {
                Vote.update($scope.vote, onSaveFinished);
            } else {
                Vote.save($scope.vote, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
}]);
