'use strict';

angular.module('chefsApp').controller('EventDialogController',
    ['$scope', '$stateParams', '$modalInstance', '$q', 'entity', 'Event', 'User', 'Recipe', 'SocialEntity',
        function($scope, $stateParams, $modalInstance, $q, entity, Event, User, Recipe, SocialEntity) {

        $scope.event = entity;
        $scope.users = User.query();
        $scope.recipes = Recipe.query();
        $scope.socialentitys = SocialEntity.query({filter: 'event-is-null'});
        $q.all([$scope.event.$promise, $scope.socialentitys.$promise]).then(function() {
            if (!$scope.event.socialEntity.id) {
                return $q.reject();
            }
            return SocialEntity.get({id : $scope.event.socialEntity.id}).$promise;
        }).then(function(socialEntity) {
            $scope.socialentitys.push(socialEntity);
        });
        $scope.load = function(id) {
            Event.get({id : id}, function(result) {
                $scope.event = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('chefsApp:eventUpdate', result);
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.event.id != null) {
                Event.update($scope.event, onSaveFinished);
            } else {
                Event.save($scope.event, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
}]);
