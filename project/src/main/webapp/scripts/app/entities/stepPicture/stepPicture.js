'use strict';

angular.module('chefsApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('stepPicture', {
                parent: 'entity',
                url: '/stepPictures',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'chefsApp.stepPicture.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/stepPicture/stepPictures.html',
                        controller: 'StepPictureController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('stepPicture');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('stepPicture.detail', {
                parent: 'entity',
                url: '/stepPicture/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'chefsApp.stepPicture.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/stepPicture/stepPicture-detail.html',
                        controller: 'StepPictureDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('stepPicture');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'StepPicture', function($stateParams, StepPicture) {
                        return StepPicture.get({id : $stateParams.id});
                    }]
                }
            })
            .state('stepPicture.new', {
                parent: 'stepPicture',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/stepPicture/stepPicture-dialog.html',
                        controller: 'StepPictureDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {title: null, src: null, properties: null, id: null};
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('stepPicture', null, { reload: true });
                    }, function() {
                        $state.go('stepPicture');
                    })
                }]
            })
            .state('stepPicture.edit', {
                parent: 'stepPicture',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/stepPicture/stepPicture-dialog.html',
                        controller: 'StepPictureDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['StepPicture', function(StepPicture) {
                                return StepPicture.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('stepPicture', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
