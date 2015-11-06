'use strict';

angular.module('chefsApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('competition', {
                parent: 'entity',
                url: '/competitions',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'chefsApp.competition.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/competition/competitions.html',
                        controller: 'CompetitionController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('competition');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('competition.detail', {
                parent: 'entity',
                url: '/competition/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'chefsApp.competition.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/competition/competition-detail.html',
                        controller: 'CompetitionDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('competition');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'Competition', function($stateParams, Competition) {
                        return Competition.get({id : $stateParams.id});
                    }]
                }
            })
            .state('competition.new', {
                parent: 'competition',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/competition/competition-dialog.html',
                        controller: 'CompetitionDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {name: null, description: null, deadline: null, rules: null, inscriptionTime: null, maxNRecipesByChefs: null, creationDate: null, completedScore: null, publicJury: null, id: null};
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('competition', null, { reload: true });
                    }, function() {
                        $state.go('competition');
                    })
                }]
            })
            .state('competition.edit', {
                parent: 'competition',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/competition/competition-dialog.html',
                        controller: 'CompetitionDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Competition', function(Competition) {
                                return Competition.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('competition', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
