'use strict';

angular.module('chefsApp').controller('CompetitionDialogController',
    ['$scope', '$stateParams', '$modalInstance', '$q', 'entity', 'Competition', 'Recipe', 'Opinion', 'User', 'SocialEntity',
        function($scope, $stateParams, $modalInstance, $q, entity, Competition, Recipe, Opinion, User, SocialEntity) {

        $scope.competition = entity;
        $scope.recipes = Recipe.query();
        $scope.opinions = Opinion.query();
        $scope.users = User.query();
        $scope.socialentitys = SocialEntity.query({filter: 'competition-is-null'});
        $q.all([$scope.competition.$promise, $scope.socialentitys.$promise]).then(function() {
            if (!$scope.competition.socialEntity.id) {
                return $q.reject();
            }
            return SocialEntity.get({id : $scope.competition.socialEntity.id}).$promise;
        }).then(function(socialEntity) {
            $scope.socialentitys.push(socialEntity);
        });
        $scope.load = function(id) {
            Competition.get({id : id}, function(result) {
                $scope.competition = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('chefsApp:competitionUpdate', result);
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.competition.id != null) {
                Competition.update($scope.competition, onSaveFinished);
            } else {
                Competition.save($scope.competition, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
}]);
