'use strict';

angular.module('chefsApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('profilePicture', {
                parent: 'entity',
                url: '/profilePictures',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'chefsApp.profilePicture.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/profilePicture/profilePictures.html',
                        controller: 'ProfilePictureController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('profilePicture');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('profilePicture.detail', {
                parent: 'entity',
                url: '/profilePicture/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'chefsApp.profilePicture.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/profilePicture/profilePicture-detail.html',
                        controller: 'ProfilePictureDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('profilePicture');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'ProfilePicture', function($stateParams, ProfilePicture) {
                        return ProfilePicture.get({id : $stateParams.id});
                    }]
                }
            })
            .state('profilePicture.new', {
                parent: 'profilePicture',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/profilePicture/profilePicture-dialog.html',
                        controller: 'ProfilePictureDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {title: null, src: null, properties: null, id: null};
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('profilePicture', null, { reload: true });
                    }, function() {
                        $state.go('profilePicture');
                    })
                }]
            })
            .state('profilePicture.edit', {
                parent: 'profilePicture',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/profilePicture/profilePicture-dialog.html',
                        controller: 'ProfilePictureDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['ProfilePicture', function(ProfilePicture) {
                                return ProfilePicture.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('profilePicture', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
