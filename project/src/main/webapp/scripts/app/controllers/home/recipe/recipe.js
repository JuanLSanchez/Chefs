'use strict';

angular.module('chefsApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('HomeRecipes', {
                parent: 'home',
                url: '/recipe',
                data: {
                    pageTitle: 'chefsApp.recipe.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/views/recipe/recipe-list-dto.html',
                        controller: 'HomeRecipeController'
                    },
                    'aside_2@': {
                        templateUrl: 'scripts/app/views/picture/recipe-pictures-module.html',
                        controller: 'PictureModuleController',
                        resolve: {
                            showPictures: ['$state', function($state) {
                                return function () { $state.go('HomeRecipePicture'); };
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
            .state('HomeRecipesDisplay', {
                parent: 'home',
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
                        $translatePartialLoader.addPart('comment');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'Recipe', function($stateParams, Recipe) {
                        return Recipe.get({id : $stateParams.id});
                    }]
                }
            })
            .state('HomeRecipeCreate',{
                parent: 'home',
                url: '/create-recipe/:name',
                views:{
                    'content@': {
                        templateUrl: 'scripts/app/views/recipe/recipe-edit.html',
                        controller: 'RecipeEditController'
                    },
                    'aside_2@': {
                        templateUrl: 'scripts/app/views/recipe/recipe-security.html',
                        controller: 'RecipeSecurityController'
                    }
                },
                resolve: {
                    entity: function ($stateParams) {
                        return {name: $stateParams.name, description: null, creationDate: new Date(), informationUrl: null, advice: null,
                            sugestedTime: null, updateDate: new Date(), ingredientsInSteps: null, id: null,
                            socialEntity:{sumRating: 0, isPublic: false, publicInscription: false, blocked: true, id: null,
                                socialPicture:{title: null, src: null, properties: null}
                            },
                            steps:[]};
                    },
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('recipe');
                        $translatePartialLoader.addPart('measurement');
                        return $translate.refresh();
                    }]
                }
            })
            .state('HomeRecipeEdit', {
                parent: 'home',
                url: '/edit-recipe/{id}',
                views:{
                    'content@': {
                        templateUrl: 'scripts/app/views/recipe/recipe-edit.html',
                        controller: 'RecipeEditController'
                    },
                    'aside_2@': {
                        templateUrl: 'scripts/app/views/recipe/recipe-security.html',
                        controller: 'RecipeSecurityController'
                    }
                },
                resolve: {
                    entity: function ($stateParams, Recipe){
                        return Recipe.get({id : $stateParams.id});
                    },
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('recipe');
                        $translatePartialLoader.addPart('measurement');
                        return $translate.refresh();
                    }]
                }
            });
    });
