'use strict';

angular.module('chefsApp')
    .controller('AssessmentsDirectiveController', function ($scope, Assessment, $state, ParseLinks, Principal, entity) {
        $scope.totalRating = "-";
        $scope.rate=null;
        var socialEntityId;

        if(entity){
            entity.$promise.then(function(response){
                socialEntityId = response.socialEntity.id;
                loadTotalRating(socialEntityId);
                loadRating(socialEntityId);
            });
        }

        var loadTotalRating = function(socialEntityId){
            Assessment.assessmentOfSocialEntity(socialEntityId).then(function(result){
                $scope.totalRating = result.data;
                if($scope.totalRating=="Infinity"){
                    $scope.totalRating = "-";
                }
            });
        };

        var loadRating = function(socialEntityId){
            Principal.identity(true).then(function(account) {
                $scope.user = account;
                if(account != null){
                    Assessment.assessmentOfSocialEntityByUser(socialEntityId).then(function(result){
                        if( result.data > 0 ){
                            $scope.rate = result.data;
                            $scope.oldRating = result.data;
                        }
                    });
                }
            });
        };

        $scope.assess = function(){
            if($scope.rate != null && $scope.rate != 0 && $scope.rate != $scope.oldRating){
                Assessment.update(socialEntityId, $scope.rate).then(function(result){
                    $scope.totalRating = result.data;
                    $scope.oldRating = $scope.rate;
                });
            }else if($scope.rate == 0){
                Assessment.delete(socialEntityId).then(function(result){
                    $scope.totalRating = result.data;
                    $scope.oldRating = null;
                    $scope.rate=null;
                });
            }
        };

    });
