/**
 * Created by juanlu on 1/12/15.
 */
'use strict';

angular.module('chefsApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('chef', {
                parent: 'entity',
                abstract: true,
                url: '/chef/{login}',
                data: {
                },
                views:{
                    'nav_1@': {
                        templateUrl: 'scripts/app/views/user/user-display-follower.html',
                        controller: 'ChefUserController'
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
                    }],
                    entity: ['$stateParams', 'User', function($stateParams, User) {
                        return User.get({login : $stateParams.login});
                    }]
                }
            })
    });
