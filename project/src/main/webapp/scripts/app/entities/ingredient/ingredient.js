'use strict';

angular.module('chefsApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('ingredient', {
                parent: 'entity',
                url: '/ingredients',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'chefsApp.ingredient.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/ingredient/ingredients.html',
                        controller: 'IngredientController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('ingredient');
                        $translatePartialLoader.addPart('measurement');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('ingredient.detail', {
                parent: 'entity',
                url: '/ingredient/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'chefsApp.ingredient.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/ingredient/ingredient-detail.html',
                        controller: 'IngredientDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('ingredient');
                        $translatePartialLoader.addPart('measurement');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'Ingredient', function($stateParams, Ingredient) {
                        return Ingredient.get({id : $stateParams.id});
                    }]
                }
            })
            .state('ingredient.new', {
                parent: 'ingredient',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/ingredient/ingredient-dialog.html',
                        controller: 'IngredientDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {amount: null, measurement: null, id: null};
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('ingredient', null, { reload: true });
                    }, function() {
                        $state.go('ingredient');
                    })
                }]
            })
            .state('ingredient.edit', {
                parent: 'ingredient',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/ingredient/ingredient-dialog.html',
                        controller: 'IngredientDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Ingredient', function(Ingredient) {
                                return Ingredient.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('ingredient', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
