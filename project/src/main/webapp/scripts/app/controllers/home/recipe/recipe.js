'use strict';

angular.module('chefsApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('home_recipe', {
                parent: 'entity',
                url: '/home/recipes',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'chefsApp.recipe.home.title'
                },
                views: {
                    'nav_1@': {
                        templateUrl: 'scripts/app/views/user/user-detail.html',
                        controller: 'HomeUserController'
                    },
                    'content@': {
                        templateUrl: 'scripts/app/views/recipe/recipes.html',
                        controller: 'HomeRecipeController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('recipe');
                        $translatePartialLoader.addPart('global');
                        $translatePartialLoader.addPart('settings');
                        return $translate.refresh();
                    }]
                }
            });
    });
