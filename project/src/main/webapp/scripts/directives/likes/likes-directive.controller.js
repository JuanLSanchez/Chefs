'use strict';

angular.module('chefsApp')
    .controller('LikesDirectiveController', function ($scope, Like, $state, ParseLinks, Principal) {
        $scope.totalLikes = "-";
        $scope.like = false;
        var socialEntityId;

        socialEntityId = $scope.socialEntityId;


        var loadTotalLikes = function(socialEntityId){
            Like.likesOfSocialEntity(socialEntityId).then(function(result){
                $scope.totalLikes = result.data;
            });
        };

        var loadLike = function(socialEntityId){
            Principal.identity(true).then(function(account) {
                $scope.user = account;
                if(account != null){
                    Like.likesOfSocialEntityByUser(socialEntityId).then(function(result){
                        $scope.like = result.data;
                    });
                }
            });
        };

        $scope.updateLike = function(){
            Like.update(socialEntityId).then(function(result){
                $scope.like = result.data;
                if($scope.like){
                    $scope.totalLikes++;
                }else{
                    $scope.totalLikes--;
                }
            });
        };

        loadTotalLikes(socialEntityId);
        loadLike(socialEntityId);

    });
