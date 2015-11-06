'use strict';

angular.module('chefsApp').controller('SocialEntityDialogController',
    ['$scope', '$stateParams', '$modalInstance', '$q', 'entity', 'SocialEntity', 'Event', 'Recipe', 'SocialPicture', 'Tag', 'Competition', 'Assessment', 'Comment', 'User',
        function($scope, $stateParams, $modalInstance, $q, entity, SocialEntity, Event, Recipe, SocialPicture, Tag, Competition, Assessment, Comment, User) {

        $scope.socialEntity = entity;
        $scope.events = Event.query();
        $scope.recipes = Recipe.query();
        $scope.socialpictures = SocialPicture.query({filter: 'socialentity-is-null'});
        $q.all([$scope.socialEntity.$promise, $scope.socialpictures.$promise]).then(function() {
            if (!$scope.socialEntity.socialPicture.id) {
                return $q.reject();
            }
            return SocialPicture.get({id : $scope.socialEntity.socialPicture.id}).$promise;
        }).then(function(socialPicture) {
            $scope.socialpictures.push(socialPicture);
        });
        $scope.tags = Tag.query();
        $scope.competitions = Competition.query();
        $scope.assessments = Assessment.query();
        $scope.comments = Comment.query();
        $scope.users = User.query();
        $scope.load = function(id) {
            SocialEntity.get({id : id}, function(result) {
                $scope.socialEntity = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('chefsApp:socialEntityUpdate', result);
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.socialEntity.id != null) {
                SocialEntity.update($scope.socialEntity, onSaveFinished);
            } else {
                SocialEntity.save($scope.socialEntity, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
}]);
