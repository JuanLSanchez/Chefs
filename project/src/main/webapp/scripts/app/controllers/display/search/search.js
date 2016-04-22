'use strict';

angular.module('chefsApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('search', {
                parent: 'display',
                url: '/search/{q}',
                params: {message:null},
                data: {
                    pageTitle: 'chefsApp.recipe.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/views/user/user-list-search.html',
                        controller: 'UserListController'
                    },
                    'content_1@': {
                        templateUrl: 'scripts/app/views/recipe/recipe-list-search.html',
                        controller: 'RecipeListController'
                    },
                    'nav_2@':{
                        templateUrl: 'scripts/app/views/tag/tag-list-search.html',
                        controller: 'TagListController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('recipe');
                        return $translate.refresh();
                    }],
                    SearchRecipe: function(Search){return Search.recipesList;}
                }
            });
    });
