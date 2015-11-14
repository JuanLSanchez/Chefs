'use strict';

angular.module('chefsApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('login', {
                parent: 'site',
                url: '/login',
                data: {
                    authorities: [],
                    pageTitle: 'login.title'
                },
                views: {
                    'welcome@': {
                        templateUrl: 'scripts/app/main/main.html',
                        controller: 'MainControllerLogin'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('main');
                        $translatePartialLoader.addPart('login');
                        $translatePartialLoader.addPart('register');
                        return $translate.refresh();
                    }]
                }
            });
    });
