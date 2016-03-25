'use strict';

angular.module('chefsApp').controller('ScheduleEditController',
        function($state, $scope, $stateParams, entity, Schedule, $uibModal, Menu, CalendarUtilities) {

        $scope.schedule = entity;
        $scope.calendar = [[[],[],[],[],[],[],[]]];
        $scope.menusToDelete = [];
        $scope.menu = null;
        $scope.oldMenu = null;

        var clearMenu = function(){
            $scope.menu = {time:new Date(0), recipes:[], id:null};
        };
        var onSaveFinished = function (result) {
            for(var week =$scope.calendar.length-1; week > -1; week--){
                for(var day = $scope.calendar[week].length-1; day > -1; day--){
                    for(var menu = $scope.calendar[week][day].length-1; menu > -1; menu--){
                        Menu.update(result.id, $scope.calendar[week][day][menu]);
                    }
                }
            }
            for(var i = $scope.menusToDelete.length-1; i>-1;i--){
                Menu.delete($scope.menusToDelete[i].id);
            }
            $state.go('HomeSchedulesDisplay', {id:result.id, message:result});
        };

        $scope.save = function () {
            if ($scope.schedule.id != null) {
                Schedule.update($scope.schedule, onSaveFinished);
            } else {
                Schedule.save($scope.schedule, onSaveFinished);
            }
        };

        /* Modal delete confirmation*/

        $scope.delete = function () {
            $scope.modal = $uibModal.open({
                templateUrl:'deleteScheduleConfirmation',
                controller: function ($scope, $state, $uibModalInstance, id) {

                    $scope.close = function() {
                        $uibModalInstance.close();
                    };

                    $scope.confirmDelete = function () {
                        Schedule.delete({id: id},
                            function (result) {
                                $uibModalInstance.close( function(result){
                                    $state.go('HomeSchedules', {message:result});
                                })
                            });
                    };
                },
                resolve:{
                    id:function(){return $scope.schedule.id;}
                }
            }).result.then(function(result) {
                    result();
                });
        };

        /* Menu Functions */
        $scope.loadMenus = function(){
            if($stateParams.id!=null){
                Menu.findAllByScheduleId($stateParams.id).then(function(result){
                    for (var i = 0; i < result.data.length; i++) {
                        addToCalendar(result.data[i], $scope.calendar);
                    }
                });
            }
        };
        $scope.menuToString = CalendarUtilities.menuToString;
        $scope.addMenu = function(week, day){
            clearMenu();
            $scope.menu.time = new Date(getMiliseconds(week, day));
            $scope.oldMenu = null;
            $scope.openMenuModal();

        };
        $scope.removeMenu = function(menu){
            var week, dayOfWeek, index;
            week = getWeek(menu.time);
            dayOfWeek = getDay(menu.time);
            index = $scope.calendar[week][dayOfWeek].indexOf(menu);
            if(index>-1){
                if(menu.id != null){
                    $scope.menusToDelete.push(menu);
                }
                $scope.calendar[week][dayOfWeek].splice(index,1);
            }
        };
        $scope.editMenu = function(menu){
            $scope.menu = menu;
            $scope.oldMenu = jQuery.extend(true, {}, menu);
            $scope.openMenuModal();
        };

        $scope.openMenuModal = function(){
            $scope.modal = $uibModal.open({
                templateUrl:'addMenuModal',
                controller: function ($scope, $state, $uibModalInstance, menu, calendar, oldMenu) {
                    $scope.mytime = new Date();
                    $scope.calendar = calendar;
                    $scope.menu = menu;
                    $scope.oldMenu = oldMenu;

                    $scope.close = function() {
                        $uibModalInstance.close(function(){
                            if(getOldMenu()!=null){
                                restartToOldMenu(oldMenu);
                            }
                        });
                    };

                    $scope.confirmMenu = function () {
                        $uibModalInstance.close(function(){
                            if(getOldMenu()==null){
                                addToCalendar($scope.menu, $scope.calendar);
                            }
                        });
                    };
                },
                resolve:{
                    menu: function(){return $scope.menu;},
                    calendar: function(){return $scope.calendar;},
                    oldMenu: function(){return $scope.oldMenu;}
                }
            }).result.then(function(result) {
                    result();
                });
        };

        /* Week functions */
        $scope.addWeek = function(){
            $scope.calendar.push([[],[],[],[],[],[],[]]);
        };
        $scope.removeWeek = function(week){

            for( var day = 0; day<7; day++){
                for( var menu = $scope.calendar[week][day].length-1; menu>-1; menu--){
                    $scope.removeMenu($scope.calendar[week][day][menu]);
                }
            }
            $scope.calendar.splice(week,1);

            for( var w = week; w<$scope.calendar.length; w++){
                for( var day = 0; day<7; day++){
                    for( var menu = $scope.calendar[w][day].length-1; menu>-1; menu--){
                        $scope.calendar[w][day][menu].time =
                            new Date($scope.calendar[w][day][menu].time.getTime()-604800000);
                    }
                }
            }
        };

        /* Functions to restart menu */
        var getOldMenu = function(){
            return $scope.oldMenu;
        };

        var restartToOldMenu = function(oldMenu){
            $scope.removeMenu($scope.menu);
            addToCalendar(oldMenu, $scope.calendar);
        };

        /* Add menu to calendar */
        var addToCalendar = CalendarUtilities.addToCalendar;
        /* Functions to the days */
        var getWeek = CalendarUtilities.getWeek;
        var getDay = CalendarUtilities.getDay;
        var getMiliseconds = CalendarUtilities.getMiliseconds;


        /* Execute */
        clearMenu();
        $scope.loadMenus();
});
