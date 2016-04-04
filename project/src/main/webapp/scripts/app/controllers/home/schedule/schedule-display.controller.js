'use strict';

angular.module('chefsApp')
    .controller('ScheduleDisplayController', function ($scope, $rootScope, $stateParams, entity, Principal,
                                                       Schedule, CalendarUtilities, Menu, $translate) {
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

        $scope.getPDF = function(){
            var text = 'Chefs';

            // Only pt supported (not mm or in)
            var doc = new jsPDF('p', 'pt');

            doc.setFontSize(18);
            doc.text($scope.schedule.name, 40, 60);
            doc.setFontSize(11);
            doc.setTextColor(100);
            doc.text(doc.splitTextToSize($scope.schedule.description, doc.internal.pageSize.width - 80, {}), 40, 80);

            if($scope.isTable){
                doc = horizontalTable(doc);
            }else{
                doc = verticalTable(doc);
            }

            doc.save($scope.schedule.name.replace(' ', '_')+'.pdf');
        };

        var horizontalTable = function(doc){
            var columns = [];
            columns.push($translate.instant('week.week'));
            for(var i = 1; i<8; i++){
                columns.push($translate.instant('week.'+i));
            }
            var rows = [];

            for(var week = 0; week < $scope.calendar.length; week++){
                rows.push([[week+1],[],[],[],[],[],[],[]]);
                for(var day = 0; day < $scope.calendar[week].length; day++){
                    for(var menu = 0; menu< $scope.calendar[week][day].length; menu++){
                        rows[week][day+1] = rows[week][day+1] + menuStr($scope.calendar[week][day][menu]) + '\n';
                    }
                }
            }

            var style = {
                overflow: 'linebreak', // visible, hidden, ellipsize or linebreak
                columnWidth: 'auto' // 'auto', 'wrap' or a number
            };

            doc.autoTable(columns, rows, {
                theme: 'grid',
                startY: 100,
                alternateRowStyles: {
                    fillColor: 250
                },
                headerStyles: {
                    fillColor: [40, 128, 186]
                },
                styles:style
            });
            return doc;
        };

        var verticalTable = function(doc){
            var columns = [];
            for(var i = 0; i<=$scope.calendar.length; i++){
                if(i!=0){
                    columns.push({title:i, dataKey:'col'+i});
                }else{
                    columns.push({title:$translate.instant('week.week'), dataKey:'col'+i});
                }
            }
            var rows = [];

            for(var day = 0; day < 7; day++){
                rows.push({'col0': $translate.instant('week.'+(day+1))})
                for(var week = 0; week < $scope.calendar.length; week++){
                    rows[day]['col'+(week+1)] = '';
                    for(var menu = 0; menu< $scope.calendar[week][day].length; menu++){
                        rows[day]['col'+(week+1)] = rows[day]['col'+(week+1)] + menuStr($scope.calendar[week][day][menu]) + '\n';
                    }
                }
            }

            var style = {
                overflow: 'linebreak', // visible, hidden, ellipsize or linebreak
                columnWidth: 'auto' // 'auto', 'wrap' or a number
            };

            doc.autoTable(columns, rows, {
                theme: 'grid', // 'striped', 'grid' or 'plain'
                startY: 100,
                alternateRowStyles: {
                    fillColor: 250
                },
                headerStyles: {
                    fillColor: [40, 128, 186]
                },
                styles:style
            });
            return doc;
        };

        var menuStr = function(menu){
            var result = '';
            if(menu!=null){
                result = $scope.menuToString(menu) + '\n ';
                for(var i in menu.recipes){
                    result = result + '-' + menu.recipes[i][0] + '\n ';
                }
            }
            return result;
        };

        /* Execute */
        loadMenus();
    });
