'use strict';

angular.module('chefsApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('listRecipes', {
                parent: 'display',
                url: '/recipes/{q}',
                params: {message:null},
                data: {
                    pageTitle: 'chefsApp.recipe.detail.title'
                },
                views: {
                    'nav_2@': {
                        templateUrl: 'scripts/app/views/assessment/assessment.html',
                        controller: 'AssessmentDisplayController'
                    },
                    'content@': {
                        templateUrl: 'scripts/app/views/recipe/recipe-list-dto.html',
                        controller: 'RecipeListController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('recipe');
                        return $translate.refresh();
                    }]
                }
            });
    });
