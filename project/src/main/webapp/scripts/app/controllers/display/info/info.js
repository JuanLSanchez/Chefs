'use strict';

angular.module('chefsApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('info', {
                parent: 'display',
                url: '/info',
                data: {
                    pageTitle: 'chefsApp.recipe.home.title'
                },
                views: {
                    'content_2@': {
                        templateUrl: 'scripts/app/views/info/info.html'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('info');
                        $translatePartialLoader.addPart('user');
                        return $translate.refresh();
                    }]
                }
            });
    });
