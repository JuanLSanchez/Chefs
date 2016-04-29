'use strict';

angular.module('chefsApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('HomeActivityLog', {
                parent: 'home',
                url: '/activityLog',
                data: {
                    pageTitle: 'chefsApp.schedule.home.title'
                },
                views: {
                    'content_2@': {
                        templateUrl: 'scripts/app/views/activityLog/activityLog-list.html',
                        controller: 'HomeActivityLog'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader',
                        function ($translate, $translatePartialLoader) {
                            return $translate.refresh();
                    }]
                }
            });
    });
