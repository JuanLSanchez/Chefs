'use strict';

angular.module('chefsApp')
    .controller('RecipePicturesController', function ($state, $timeout, $scope, $stateParams, RecipeUser, ParseLinks) {
        $scope.pictures = [];
        $scope.pictureDetails = {};
        $scope.pictureIndex = 0;
        $scope.page = 0;
        $scope.loadAll = function() {
            RecipeUser.get({login:$stateParams.login ,page: $scope.page, size: 10}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                for (var i = 0; i < result.length; i++) {
                    if(result[i].socialEntity.socialPicture.src != null) {
                        $scope.pictures.push({
                            src: result[i].socialEntity.socialPicture.src, recipe: result[i].id,
                            recipeName: result[i].name, title: result[i].socialEntity.socialPicture.title,
                            updateDate: result[i].updateDate, class: 'col-xs-4'
                        });
                    }
                    for (var j = 0; j < result[i].steps.length; j++ ){
                        for (var k = 0; k < result[i].steps[j].stepPicture.length; k++){
                            if(result[i].steps[j].stepPicture[k].src != null) {
                                $scope.pictures.push({
                                    src: result[i].steps[j].stepPicture[k].src, recipe: result[i].id,
                                    recipeName: result[i].name, title: result[i].steps[j].stepPicture[k].title,
                                    updateDate: result[i].updateDate, class: 'col-xs-4'
                                });
                            }
                        }
                    }
                }
            });
        };
        $scope.reset = function() {
            $scope.page = 0;
            $scope.pictures = [];
            $scope.loadAll();
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.refresh = function () {
            $scope.reset();
            $scope.clear();
        };

        $scope.detailsOfPicture = function(pictureIndex){
            $scope.pictureDetails = $scope.pictures[pictureIndex];
            $scope.pictureIndex = pictureIndex;
        };

        $scope.nextPicture = function(){
            if ($scope.pictureIndex < $scope.pictures.length - 1 ){
                $scope.pictureIndex = $scope.pictureIndex + 1;
                $scope.pictureDetails = $scope.pictures[$scope.pictureIndex];
            }
        };

        $scope.prevPicture = function(){
            if ($scope.pictureIndex > 0 ){
                $scope.pictureIndex = $scope.pictureIndex - 1;
                $scope.pictureDetails = $scope.pictures[$scope.pictureIndex];
            }
        };

        $scope.cancel = function () {
            $timeout($state.go('ChefRecipeDisplay', {id:$scope.pictureDetails.recipe}), 3000);
        };

        $scope.clear = function () {
            $scope.pictures = {src:null, recipe:null, recipeName:null, title:null, updateDate:null};
        };
    });
