'use strict';

angular.module('chefsApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('assessment', {
                parent: 'entity',
                url: '/assessments',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'chefsApp.assessment.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/assessment/assessments.html',
                        controller: 'AssessmentController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('assessment');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('assessment.detail', {
                parent: 'entity',
                url: '/assessment/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'chefsApp.assessment.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/assessment/assessment-detail.html',
                        controller: 'AssessmentDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('assessment');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'Assessment', function($stateParams, Assessment) {
                        return Assessment.get({id : $stateParams.id});
                    }]
                }
            })
            .state('assessment.new', {
                parent: 'assessment',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/assessment/assessment-dialog.html',
                        controller: 'AssessmentDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {rating: null, id: null};
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('assessment', null, { reload: true });
                    }, function() {
                        $state.go('assessment');
                    })
                }]
            })
            .state('assessment.edit', {
                parent: 'assessment',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/assessment/assessment-dialog.html',
                        controller: 'AssessmentDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Assessment', function(Assessment) {
                                return Assessment.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('assessment', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
