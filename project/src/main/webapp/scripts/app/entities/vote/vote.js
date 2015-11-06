'use strict';

angular.module('chefsApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('vote', {
                parent: 'entity',
                url: '/votes',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'chefsApp.vote.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/vote/votes.html',
                        controller: 'VoteController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('vote');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('vote.detail', {
                parent: 'entity',
                url: '/vote/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'chefsApp.vote.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/vote/vote-detail.html',
                        controller: 'VoteDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('vote');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'Vote', function($stateParams, Vote) {
                        return Vote.get({id : $stateParams.id});
                    }]
                }
            })
            .state('vote.new', {
                parent: 'vote',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/vote/vote-dialog.html',
                        controller: 'VoteDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {isFinal: null, abstain: null, comment: null, completedScore: null, id: null};
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('vote', null, { reload: true });
                    }, function() {
                        $state.go('vote');
                    })
                }]
            })
            .state('vote.edit', {
                parent: 'vote',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/vote/vote-dialog.html',
                        controller: 'VoteDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Vote', function(Vote) {
                                return Vote.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('vote', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
