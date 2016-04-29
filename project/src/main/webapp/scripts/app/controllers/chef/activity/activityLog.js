'use strict';

angular.module('chefsApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('ChefActivityLog', {
                parent: 'chef',
                url: '/activityLog',
                data: {
                    pageTitle: 'chefsApp.schedule.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/views/activityLog/activityLog-list.html',
                        controller: 'ChefActivityLog'
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
