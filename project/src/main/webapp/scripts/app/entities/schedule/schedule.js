'use strict';

angular.module('chefsApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('schedule', {
                parent: 'entity',
                url: '/schedules',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'chefsApp.schedule.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/schedule/schedules.html',
                        controller: 'ScheduleController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('schedule');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('schedule.detail', {
                parent: 'entity',
                url: '/schedule/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'chefsApp.schedule.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/schedule/schedule-detail.html',
                        controller: 'ScheduleDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('schedule');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'Schedule', function($stateParams, Schedule) {
                        return Schedule.get({id : $stateParams.id});
                    }]
                }
            })
            .state('schedule.new', {
                parent: 'schedule',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/schedule/schedule-dialog.html',
                        controller: 'ScheduleDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {name: null, description: null, id: null};
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('schedule', null, { reload: true });
                    }, function() {
                        $state.go('schedule');
                    })
                }]
            })
            .state('schedule.edit', {
                parent: 'schedule',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/schedule/schedule-dialog.html',
                        controller: 'ScheduleDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Schedule', function(Schedule) {
                                return Schedule.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('schedule', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
