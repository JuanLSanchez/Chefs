'use strict';

angular.module('chefsApp').controller('OpinionDialogController',
    ['$scope', '$stateParams', '$modalInstance', 'entity', 'Opinion', 'Score', 'Competition',
        function($scope, $stateParams, $modalInstance, entity, Opinion, Score, Competition) {

        $scope.opinion = entity;
        $scope.scores = Score.query();
        $scope.competitions = Competition.query();
        $scope.load = function(id) {
            Opinion.get({id : id}, function(result) {
                $scope.opinion = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('chefsApp:opinionUpdate', result);
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.opinion.id != null) {
                Opinion.update($scope.opinion, onSaveFinished);
            } else {
                Opinion.save($scope.opinion, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
}]);
