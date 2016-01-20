'use strict';

angular.module('chefsApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('home_recipe-pictures', {
                parent: 'display',
                url: '/recipe-pictures/{login}',
                data: {
                    pageTitle: 'chefsApp.recipe.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/views/picture/recipe-pictures.html',
                        controller: 'RecipePicturesController'
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
