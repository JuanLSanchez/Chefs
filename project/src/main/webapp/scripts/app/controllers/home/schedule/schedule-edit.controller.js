'use strict';

angular.module('chefsApp').controller('ScheduleEditController',
        function($state, $scope, $stateParams, $rootScope, entity, Schedule, $uibModal, Menu, CalendarUtilities) {

        $scope.schedule = entity;
        $scope.calendar = [[[],[],[],[],[],[],[]]];
        $scope.menusToDelete = [];
        $scope.menu = null;
        $scope.oldMenu = null;
        $scope.isTable = true;
        $scope.recipesToRemove = {};
        var name=entity.name, description=entity.name;

        $scope.switchView = function(){
            $scope.isTable = !$scope.isTable;
        };

        $scope.start = function(){
            Schedule.save($scope.schedule, function(result){
                $scope.schedule = result;
                name=result.name;
                description = result.description;
            });
        };

        $scope.update = function(invalid){
            if(!invalid && $scope.schedule.id!=null&&(name!=$scope.schedule.name||description!=$scope.schedule.description)){
                Schedule.update($scope.schedule, function(result){
                    $scope.schedule = result;
                    name=result.name;
                    description = result.description;
                });
            }
        };

        $scope.finish = function(){
            $state.go('HomeSchedulesDisplay',{id:$scope.schedule.id});
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

        $scope.openMenuModal = function(){
            $scope.modal = $uibModal.open({
                templateUrl:'addMenuModal',
                controller: 'MenuModalEditController',
                resolve:{
                    menu: function(){return $scope.menu;},
                    calendar: function(){return $scope.calendar;},
                    oldMenu: function(){return $scope.oldMenu;},
                    schedule: function(){return $scope.schedule;}
                }
            }).result.then(function(result) {
                    $scope.menu.id = result().id;
                    $scope.menu.time = result().time;
                    $scope.menu.recipes = result().recipes;
                });
        };

        $scope.removeMenu = function(menu){
            var week, dayOfWeek, index;
            week = getWeek(menu.time);
            dayOfWeek = getDay(menu.time);
            index = $scope.calendar[week][dayOfWeek].indexOf(menu);
            if(index>-1){
                Menu.delete(menu.id);
                $scope.calendar[week][dayOfWeek].splice(index,1);
            }
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
        $scope.editMenu = function(menu){
            $scope.menu = menu;
            $scope.oldMenu = jQuery.extend(true, {}, menu);
            $scope.openMenuModal();
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

        var clearMenu = function(){
            $scope.menu = {time:new Date(0), recipes:{}, id:null};
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
