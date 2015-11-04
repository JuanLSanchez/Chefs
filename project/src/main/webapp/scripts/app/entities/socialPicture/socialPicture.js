'use strict';

angular.module('chefsApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('socialPicture', {
                parent: 'entity',
                url: '/socialPictures',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'chefsApp.socialPicture.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/socialPicture/socialPictures.html',
                        controller: 'SocialPictureController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('socialPicture');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('socialPicture.detail', {
                parent: 'entity',
                url: '/socialPicture/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'chefsApp.socialPicture.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/socialPicture/socialPicture-detail.html',
                        controller: 'SocialPictureDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('socialPicture');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'SocialPicture', function($stateParams, SocialPicture) {
                        return SocialPicture.get({id : $stateParams.id});
                    }]
                }
            })
            .state('socialPicture.new', {
                parent: 'socialPicture',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/socialPicture/socialPicture-dialog.html',
                        controller: 'SocialPictureDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {title: null, url: null, properties: null, id: null};
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('socialPicture', null, { reload: true });
                    }, function() {
                        $state.go('socialPicture');
                    })
                }]
            })
            .state('socialPicture.edit', {
                parent: 'socialPicture',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/socialPicture/socialPicture-dialog.html',
                        controller: 'SocialPictureDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['SocialPicture', function(SocialPicture) {
                                return SocialPicture.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('socialPicture', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
