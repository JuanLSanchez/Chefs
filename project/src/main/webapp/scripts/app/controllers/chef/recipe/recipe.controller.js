'use strict';

angular.module('chefsApp')
    .controller('ChefRecipeController', function ($scope, $state, $stateParams, $rootScope, RecipeUser, ParseLinks) {
        $scope.recipes = [];
        $scope.page = 0;
        $rootScope.pictures = [];
        $scope.loadAll = function() {
            RecipeUser.query({login:$stateParams.login,page: $scope.page, size: 10}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                for (var i = 0; i < result.length; i++) {
                    $scope.recipes.push(result[i]);
                    if ($rootScope.pictures.length < 4){
                        if(result[i].socialEntity.socialPicture.src != null) {
                            $rootScope.pictures.push({
                                src: result[i].socialEntity.socialPicture.src, recipe: result[i].id,
                                recipeName: result[i].name, title: result[i].socialEntity.socialPicture.title,
                                updateDate: result[i].updateDate, class: 'col-lg-4'
                            });
                        }
                        for (var j = 0; j < result[i].steps.length && $rootScope.pictures.length < 4; j++ ){
                            for (var k = 0; k < result[i].steps[j].stepPicture.length
                            && $rootScope.pictures.length < 4; k++){
                                if(result[i].steps[j].stepPicture[k].src != null) {
                                    $rootScope  .pictures.push({
                                        src: result[i].steps[j].stepPicture[k].src, recipe: result[i].id,
                                        recipeName: result[i].name, title: result[i].steps[j].stepPicture[k].title,
                                        updateDate: result[i].updateDate, class: 'col-lg-4'
                                    });
                                }
                            }
                        }

                    }
                }
            });
        };
        $scope.reset = function() {
            $scope.page = 0;
            $scope.recipes = [];
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

        $scope.clear = function () {
            $scope.recipe = {name: null, description: null, creationDate: null, informationUrl: null, advice: null, sugestedTime: null, updateDate: null, ingredientsInSteps: null, id: null};
        };
        $scope.showRecipe = function (param) {
            param['login']=$stateParams.login;
            $state.go("ChefRecipeDisplay", param);
        };
    });
