'use strict';

angular.module('chefsApp')
    .controller('ScheduleDisplayController', function ($scope, $rootScope, $stateParams, entity, Principal,
                                                       Schedule, CalendarUtilities, Menu) {
        $scope.schedule = entity;
        $scope.calendar = [];
        $scope.isTable = true;

        $scope.switchView = function(){
            $scope.isTable = !$scope.isTable;
        };


        var oneTime = {
            remind: function() {
                loadMenus();
                this.timeoutID = undefined;
            },

            setup: function() {
                if (typeof this.timeoutID === "number") {
                    this.cancel();
                }
                this.timeoutID = window.setTimeout(function() {
                    this.remind();
                }.bind(this), 500);
            },

            cancel: function() {
                window.clearTimeout(this.timeoutID);
                this.timeoutID = undefined;
            }
        };

        /* Menu Functions */
        var loadMenus = function(){
            if($stateParams.id!=null){
                $scope.calendar = [];
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


        $rootScope.$on('chefsApp:scheduleUpdate', function(event, result) {
            $scope.schedule = result;
        });
        $rootScope.$on('chefsApp:menuUpdate', function(event, result) {
            if(result.schedule.id==$stateParams.id){
                oneTime.setup();
            }
        });

        /* Execute */
        loadMenus();
    });
