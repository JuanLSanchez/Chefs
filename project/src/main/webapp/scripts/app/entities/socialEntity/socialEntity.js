'use strict';

angular.module('chefsApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('socialEntity', {
                parent: 'entity',
                url: '/socialEntitys',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'chefsApp.socialEntity.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/socialEntity/socialEntitys.html',
                        controller: 'SocialEntityController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('socialEntity');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('socialEntity.detail', {
                parent: 'entity',
                url: '/socialEntity/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'chefsApp.socialEntity.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/socialEntity/socialEntity-detail.html',
                        controller: 'SocialEntityDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('socialEntity');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'SocialEntity', function($stateParams, SocialEntity) {
                        return SocialEntity.get({id : $stateParams.id});
                    }]
                }
            })
            .state('socialEntity.new', {
                parent: 'socialEntity',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/socialEntity/socialEntity-dialog.html',
                        controller: 'SocialEntityDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {sumRating: null, isPublic: null, publicInscription: null, blocked: null, id: null};
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('socialEntity', null, { reload: true });
                    }, function() {
                        $state.go('socialEntity');
                    })
                }]
            })
            .state('socialEntity.edit', {
                parent: 'socialEntity',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/socialEntity/socialEntity-dialog.html',
                        controller: 'SocialEntityDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['SocialEntity', function(SocialEntity) {
                                return SocialEntity.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('socialEntity', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
