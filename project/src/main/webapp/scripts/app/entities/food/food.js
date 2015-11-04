'use strict';

angular.module('chefsApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('food', {
                parent: 'entity',
                url: '/foods',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'chefsApp.food.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/food/foods.html',
                        controller: 'FoodController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('food');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('food.detail', {
                parent: 'entity',
                url: '/food/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'chefsApp.food.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/food/food-detail.html',
                        controller: 'FoodDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('food');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'Food', function($stateParams, Food) {
                        return Food.get({id : $stateParams.id});
                    }]
                }
            })
            .state('food.new', {
                parent: 'food',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/food/food-dialog.html',
                        controller: 'FoodDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {normalizaedName: null, name: null, kcal: null, id: null};
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('food', null, { reload: true });
                    }, function() {
                        $state.go('food');
                    })
                }]
            })
            .state('food.edit', {
                parent: 'food',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/food/food-dialog.html',
                        controller: 'FoodDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Food', function(Food) {
                                return Food.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('food', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
