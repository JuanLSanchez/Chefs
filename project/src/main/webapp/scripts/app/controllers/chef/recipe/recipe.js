'use strict';

angular.module('chefsApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('user.recipe',{
                parent: 'user',
                url: '/recipe/:name',
                views:{
                    'content@': {
                        templateUrl: 'scripts/app/views/recipe/recipe-edit.html',
                        controller: 'RecipeEditController'
                    },
                    'aside_2@': {
                        templateUrl: 'scripts/app/views/recipe/recipe-security.html',
                        controller: 'RecipeSecurityController'
                    }
                },
                resolve: {
                    entity: function ($stateParams) {
                        return {name: $stateParams.name, description: null, creationDate: new Date(), informationUrl: null, advice: null,
                            sugestedTime: null, updateDate: new Date(), ingredientsInSteps: null, id: null,
                            socialEntity:{sumRating: 0, isPublic: false, publicInscription: false, blocked: true, id: null,
                                socialPicture:{title: null, src: null, properties: null}
                            },
                            steps:[]};
                    },
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('recipe');
                        $translatePartialLoader.addPart('measurement');
                        return $translate.refresh();
                    }]
                }
            })
            .state('user.recipe.edit', {
                parent: 'user',
                url: '/recipe/{id}/edit',
                views:{
                    'content@': {
                        templateUrl: 'scripts/app/views/recipe/recipe-edit.html',
                        controller: 'RecipeEditController'
                    },
                    'aside_2@': {
                        templateUrl: 'scripts/app/views/recipe/recipe-security.html',
                        controller: 'RecipeSecurityController'
                    }
                },
                resolve: {
                    entity: function ($stateParams, Recipe){
                        return Recipe.get({id : $stateParams.id});
                    },
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('recipe');
                        $translatePartialLoader.addPart('measurement');
                        return $translate.refresh();
                    }]
                }
            });
    });
