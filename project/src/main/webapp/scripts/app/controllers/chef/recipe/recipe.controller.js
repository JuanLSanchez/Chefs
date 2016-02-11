'use strict';

angular.module('chefsApp')
    .controller('ChefRecipeController', function ($scope, $state, $stateParams, $rootScope, RecipeUserDTO, ParseLinks) {
        $scope.recipes = [];
        $scope.page = 0;
        $scope.pageSize = 4;
        $rootScope.pictures = [];
        $scope.loadAll = function() {
            RecipeUserDTO.query({login:$stateParams.login,page: $scope.page, size: $scope.pageSize}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                for (var i = 0; i < result.length; i++) {
                    $scope.recipes.push(result[i]);
                    if ($rootScope.pictures.length < 4){
                        if(result[i].picture != null) {
                            $rootScope.pictures.push({
                                src: result[i].picture, recipe: result[i].id,
                                recipeName: result[i].name,
                                updateDate: result[i].updateDate, class: 'col-lg-4'
                            });
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

        $scope.showRecipe = function (param) {
            param['login']=$stateParams.login;
            $state.go("ChefRecipeDisplay", param);
        };
    });
