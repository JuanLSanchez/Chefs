'use strict';

angular.module('chefsApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('listUser', {
                parent: 'display',
                url: '/users/{q}',
                params: {message:null},
                data: {
                    pageTitle: 'chefsApp.recipe.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/views/user/user-list.html',
                        controller: 'UserListController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('user');
                        return $translate.refresh();
                    }]
                }
            });
    });
