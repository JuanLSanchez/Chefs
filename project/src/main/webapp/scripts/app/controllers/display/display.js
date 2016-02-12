/**
 * Created by juanlu on 1/12/15.
 */
'use strict';

angular.module('chefsApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('display', {
                parent: 'entity',
                abstract: true,
                url: '/display',
                data: {
                },
                views:{
                    'nav_1@': {
                        templateUrl: 'scripts/app/views/user/user-display.html',
                        controller: 'PrincipalUserController'
                    },
                    'aside_1@': {
                        templateUrl: 'scripts/app/views/creator/creator.html'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('global');
                        $translatePartialLoader.addPart('settings');
                        return $translate.refresh();
                    }]
                }
            });
    });
