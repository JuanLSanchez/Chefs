'use strict';

angular.module('chefsApp')
    .controller('ScheduleDisplayController', function ($scope, $rootScope, $stateParams, entity, Principal,
                                                       Schedule, CalendarUtilities, Menu) {
        $scope.schedule = entity;
        $scope.calendar = [];
        $scope.isTable = true;
        Principal.identity(true).then(function(account) {
            $scope.userAccount = account;
        });
        $scope.load = function (id) {
            Schedule.get({id: id}, function(result) {
                $scope.schedule = result;
            });
            if($stateParams.message!=null){
                $scope.$emit('chefsApp:scheduleUpdate', $stateParams.message);
            }
        };
        $scope.switchView = function(){
            $scope.isTable = !$scope.isTable;
        };

        /* Menu Functions */
        var loadMenus = function(){
            if($stateParams.id!=null){
                Menu.findAllByScheduleId($stateParams.id).then(function(result){
                    for (var i = 0; i < result.data.length; i++) {
                        addToCalendar(result.data[i], $scope.calendar);
                    }
                });
            }
        };
        $scope.menuToString = CalendarUtilities.menuToString;
        /* Add menu to calendar */
        var addToCalendar = CalendarUtilities.addToCalendar;
        /* Functions to the days */
        var getWeek = CalendarUtilities.getWeek;
        var getDay = CalendarUtilities.getDay;
        var getMiliseconds = CalendarUtilities.getMiliseconds;


        $rootScope.$on('chefsApp:scheduleUpdate', function(event, result) {
            $scope.schedule = result;
        });

        /* Execute */
        loadMenus();
    });
