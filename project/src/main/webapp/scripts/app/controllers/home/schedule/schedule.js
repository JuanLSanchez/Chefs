'use strict';

angular.module('chefsApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('HomeSchedules', {
                parent: 'home',
                url: '/schedule',
                data: {
                    pageTitle: 'chefsApp.schedule.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/views/schedule/schedule-list.html',
                        controller: 'HomeScheduleController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader',
                        function ($translate, $translatePartialLoader) {
                            $translatePartialLoader.addPart('schedule');
                            return $translate.refresh();
                    }]
                }
            })
            .state('HomeSchedulesDisplay', {
                parent: 'home',
                url: '/schedule/{id}',
                params: {message:null},
                data: {
                    pageTitle: 'chefsApp.schedule.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/views/schedule/schedule-display.html',
                        controller: 'ScheduleDisplayController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('schedule');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'Schedule', function($stateParams, Schedule) {
                        return Schedule.get({id : $stateParams.id});
                    }]
                }
            })
            .state('HomeSchedulesCreate',{
                parent: 'home',
                url: '/schedule/create/:name',
                data: {
                    pageTitle: 'chefsApp.schedule.home.title'
                },
                views:{
                    'content@': {
                        templateUrl: 'scripts/app/views/schedule/schedule-edit.html',
                        controller: 'ScheduleEditController'
                    }
                },
                resolve: {
                    entity: function ($stateParams) {
                        return {"id": null,
                                "name": $stateParams.name,
                                "description": null };
                    },
                    translatePartialLoader: ['$translate', '$translatePartialLoader',
                        function ($translate, $translatePartialLoader){
                            $translatePartialLoader.addPart('schedule');
                            return $translate.refresh();
                    }]
                }
            })
            .state('HomeSchedulesEdit', {
                parent: 'home',
                url: '/schedule/edit/{id}',
                data: {
                    pageTitle: 'chefsApp.schedule.home.title'
                },
                views:{
                    'content@': {
                        templateUrl: 'scripts/app/views/schedule/schedule-edit.html',
                        controller: 'ScheduleEditController'
                    }
                },
                resolve: {
                    entity: function ($stateParams, Schedule){
                        return Schedule.get({id : $stateParams.id});
                    },
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('schedule');
                        return $translate.refresh();
                    }]
                }
            });
    });
