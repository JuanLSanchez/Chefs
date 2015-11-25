'use strict';

angular.module('chefsApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('backgroundPicture', {
                parent: 'entity',
                url: '/backgroundPictures',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'chefsApp.backgroundPicture.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/backgroundPicture/backgroundPictures.html',
                        controller: 'BackgroundPictureController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('backgroundPicture');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('backgroundPicture.detail', {
                parent: 'entity',
                url: '/backgroundPicture/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'chefsApp.backgroundPicture.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/backgroundPicture/backgroundPicture-detail.html',
                        controller: 'BackgroundPictureDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('backgroundPicture');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'BackgroundPicture', function($stateParams, BackgroundPicture) {
                        return BackgroundPicture.get({id : $stateParams.id});
                    }]
                }
            })
            .state('backgroundPicture.new', {
                parent: 'backgroundPicture',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/backgroundPicture/backgroundPicture-dialog.html',
                        controller: 'BackgroundPictureDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {title: null, src: null, properties: null, id: null};
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('backgroundPicture', null, { reload: true });
                    }, function() {
                        $state.go('backgroundPicture');
                    })
                }]
            })
            .state('backgroundPicture.edit', {
                parent: 'backgroundPicture',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/backgroundPicture/backgroundPicture-dialog.html',
                        controller: 'BackgroundPictureDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['BackgroundPicture', function(BackgroundPicture) {
                                return BackgroundPicture.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('backgroundPicture', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
