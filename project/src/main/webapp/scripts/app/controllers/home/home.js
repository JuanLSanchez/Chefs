/**
 * Created by juanlu on 1/12/15.
 */
'use strict';

angular.module('chefsApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('home', {
                parent: 'entity',
                abstract: true,
                url: '/home',
                data: {
                    authorities: ['ROLE_USER'],
                },
                views:{
                    'nav_1@': {
                        templateUrl: 'scripts/app/views/user/user-edit.html',
                        controller: 'HomeUserController'
                    },
                    'aside_1@': {
                        templateUrl: 'scripts/app/views/creator/creator.html'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('global');
                        $translatePartialLoader.addPart('settings');
                        $translatePartialLoader.addPart('user');
                        return $translate.refresh();
                    }]
                }
            })
    });
