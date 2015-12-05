'use strict';

angular.module('chefsApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('display_recipe', {
                parent: 'display',
                url: '/recipe/{id}',
                params: {message:null},
                data: {
                    pageTitle: 'chefsApp.recipe.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/views/recipe/recipe-display.html',
                        controller: 'RecipeDisplayController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('recipe');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'Recipe', function($stateParams, Recipe) {
                        return Recipe.get({id : $stateParams.id});
                    }]
                }
            });
    });
