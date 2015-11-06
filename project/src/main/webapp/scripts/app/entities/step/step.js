'use strict';

angular.module('chefsApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('step', {
                parent: 'entity',
                url: '/steps',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'chefsApp.step.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/step/steps.html',
                        controller: 'StepController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('step');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('step.detail', {
                parent: 'entity',
                url: '/step/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'chefsApp.step.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/step/step-detail.html',
                        controller: 'StepDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('step');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'Step', function($stateParams, Step) {
                        return Step.get({id : $stateParams.id});
                    }]
                }
            })
            .state('step.new', {
                parent: 'step',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/step/step-dialog.html',
                        controller: 'StepDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {position: null, section: null, id: null};
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('step', null, { reload: true });
                    }, function() {
                        $state.go('step');
                    })
                }]
            })
            .state('step.edit', {
                parent: 'step',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/step/step-dialog.html',
                        controller: 'StepDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Step', function(Step) {
                                return Step.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('step', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
