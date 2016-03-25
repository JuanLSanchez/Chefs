'use strict';

angular.module('chefsApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('recipe', {
                parent: 'entity',
                url: '/recipes',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'chefsApp.recipe.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/recipe/recipes.html',
                        controller: 'RecipeController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('recipe');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('recipe.detail', {
                parent: 'entity',
                url: '/recipe/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'chefsApp.recipe.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/recipe/recipe-detail.html',
                        controller: 'RecipeDetailController'
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
            })
            .state('recipe.new', {
                parent: 'recipe',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/recipe/recipe-dialog.html',
                        controller: 'RecipeDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {name: null, description: null, creationDate: null, informationUrl: null, advice: null, sugestedTime: null, updateDate: null, ingredientsInSteps: null, id: null};
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('recipe', null, { reload: true });
                    }, function() {
                        $state.go('recipe');
                    })
                }]
            })
            .state('recipe.edit', {
                parent: 'recipe',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/recipe/recipe-dialog.html',
                        controller: 'RecipeDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Recipe', function(Recipe) {
                                return Recipe.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('recipe', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
