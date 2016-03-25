'use strict';

angular.module('chefsApp').controller('ScoreDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'Score', 'Vote', 'Opinion',
        function($scope, $stateParams, $uibModalInstance, entity, Score, Vote, Opinion) {

        $scope.score = entity;
        $scope.votes = Vote.query();
        $scope.opinions = Opinion.query();
        $scope.load = function(id) {
            Score.get({id : id}, function(result) {
                $scope.score = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('chefsApp:scoreUpdate', result);
            $uibModalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.score.id != null) {
                Score.update($scope.score, onSaveFinished);
            } else {
                Score.save($scope.score, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
}]);
