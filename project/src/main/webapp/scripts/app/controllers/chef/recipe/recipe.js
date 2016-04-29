'use strict';

angular.module('chefsApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('ChefRecipes', {
                parent: 'chef',
                url: '/recipe',
                data: {
                    pageTitle: 'chefsApp.recipe.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/views/recipe/recipe-list-dto.html',
                        controller: 'ChefRecipeController'
                    },
                    'aside_2@': {
                        templateUrl: 'scripts/app/views/picture/recipe-pictures-module.html',
                        controller: 'PictureModuleController',
                        resolve: {
                            showPictures: ['$state', function($state) {
                                return function(){ $state.go('ChefRecipePicture')};
                            }]
                        }
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('recipe');
                        return $translate.refresh();
                    }]
                }
            })
            .state('ChefRecipeDisplay', {
                parent: 'chef',
                url: '/recipe/{id}',
                params: {message:null},
                data: {
                    pageTitle: 'chefsApp.recipe.detail.title'
                },
                views: {
                    'nav_2@': {
                        templateUrl: 'scripts/app/views/assessment/assessment.html',
                        controller: 'AssessmentDisplayController'
                    },
                    'content@': {
                        templateUrl: 'scripts/app/views/recipe/recipe-display.html',
                        controller: 'RecipeDisplayController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('recipe');
                        $translatePartialLoader.addPart('comment');
                        $translatePartialLoader.addPart('assessment');
                        $translatePartialLoader.addPart('socialEntity');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'Recipe', '$rootScope', function($stateParams, Recipe, $rootScope) {
                        return Recipe.get({id : $stateParams.id},
                            function(result){return result;},
                            function(response){$rootScope.$emit('chefsApp:recipeNotFound', response);});
                    }]
                }
            });
    });
