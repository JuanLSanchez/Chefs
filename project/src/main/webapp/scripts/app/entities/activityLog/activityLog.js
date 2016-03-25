'use strict';

angular.module('chefsApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('activityLog', {
                parent: 'entity',
                url: '/activityLogs',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'chefsApp.activityLog.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/activityLog/activityLogs.html',
                        controller: 'ActivityLogController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('activityLog');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('activityLog.detail', {
                parent: 'entity',
                url: '/activityLog/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'chefsApp.activityLog.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/activityLog/activityLog-detail.html',
                        controller: 'ActivityLogDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('activityLog');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'ActivityLog', function($stateParams, ActivityLog) {
                        return ActivityLog.get({id : $stateParams.id});
                    }]
                }
            })
            .state('activityLog.new', {
                parent: 'activityLog',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/activityLog/activityLog-dialog.html',
                        controller: 'ActivityLogDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {idOfCustomer: null, nameOfCustomer: null, pictureUrl: null, objectType: null, verb: null, moment: null, name: null, description: null, tags: null, id: null};
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('activityLog', null, { reload: true });
                    }, function() {
                        $state.go('activityLog');
                    })
                }]
            })
            .state('activityLog.edit', {
                parent: 'activityLog',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/activityLog/activityLog-dialog.html',
                        controller: 'ActivityLogDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['ActivityLog', function(ActivityLog) {
                                return ActivityLog.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('activityLog', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
