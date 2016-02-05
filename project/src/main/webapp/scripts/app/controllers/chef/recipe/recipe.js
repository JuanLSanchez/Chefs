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
                        templateUrl: 'scripts/app/views/recipe/recipe-list.html',
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
                    'content@': {
                        templateUrl: 'scripts/app/views/recipe/recipe-display.html',
                        controller: 'RecipeDisplayController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('recipe');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'Recipe', function($stateParams, Recipe) {
                        return Recipe.get({id : $stateParams.id});
                    }]
                }
            });
    });
