'use strict';

angular.module('chefsApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('ChefRecipePicture', {
                parent: 'chef',
                url: '/pictures',
                data: {
                    pageTitle: 'chefsApp.recipe.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/views/picture/recipe-pictures.html',
                        controller: 'RecipePicturesController',
                        resolve: {
                            site: function(){return 'chef'}
                        }
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('recipe');
                        return $translate.refresh();
                    }],
                    site: function(){return 'chef'}
                }
            });
    });
