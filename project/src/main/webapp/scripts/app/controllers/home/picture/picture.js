'use strict';

angular.module('chefsApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('HomeRecipePicture', {
                parent: 'home',
                url: '/pictures/{login}',
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
