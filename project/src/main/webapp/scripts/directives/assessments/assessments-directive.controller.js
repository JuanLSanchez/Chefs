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

/*         $scope.comments = [];
        $scope.page = 0;
        $scope.comment;

       //Get principal
        Principal.identity(true).then(function(account) {
            $scope.user = account;
        });

        var cleanComment = function(){
          $scope.comment = {creationMoment:new Date(), body:null, id:null};
        };
        cleanComment();
        $scope.loadAll = function() {
                Comment.findAllOfSE($scope.socialEntityId, {page: $scope.page, size: 20}).then(
                    function (result) {
                        $scope.links = ParseLinks.parse(result.headers('link'));
                        for (var i = 0; i < result.data.length; i++) {
                            $scope.comments.push(result.data[i]);
                        }
                    });
        };
        $scope.reset = function() {
            $scope.page = 0;
            $scope.comments = [];
            $scope.loadAll();
        };
        $scope.loadPage = function(page) {
            $scope.page=page;
            $scope.loadAll();
        };

        $scope.loadAll();

        $scope.save = function(){
            if($scope.comment.id==null){
                Comment.save($scope.socialEntityId, $scope.comment.body).then(function(result){
                    $scope.comments.push(result.data);
                    cleanComment();
                });
            }else{
                Comment.update($scope.comment.id, $scope.comment.body).then(function(result){
                    $scope.comment = result.data;
                    cleanComment();
                });
            }
        };

        $scope.delete = function(comment){
            Comment.delete(comment.id).then(function(){
                var index = $scope.comments.indexOf(comment);
                if(index > -1){
                    $scope.comments.splice(index, 1);
                }
            });
        };

        $scope.edit = function(comment){
            $scope.comment = comment;
        };*/


    });
