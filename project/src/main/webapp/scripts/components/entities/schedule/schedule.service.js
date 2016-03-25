'use strict';

angular.module('chefsApp')
    .factory('Schedule', function ($resource, DateUtils) {
        return $resource('api/schedules/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    });

angular.module('chefsApp')
    .factory('CalendarUtilities', function () {
        var getWeek = function(date){
            return Math.floor(date.getTime()/(604800000));
        };
        var getDay = function(date){
            return Math.floor(date.getTime()/(86400000)%7);
        };
        var addToCalendar = function(menu, calendar){
            var week, dayOfWeek;
            week = getWeek(menu.time);
            dayOfWeek = getDay(menu.time);
            for( var i = calendar.length; i<week+1; i++ ){
                calendar[i]=[[],[],[],[],[],[],[]];
            }
            calendar[week][dayOfWeek].push(menu);
        };
        var menuToString = function(menu){
            return menu.time.getHours()+':'+('0'+menu.time.getMinutes()).slice(-2);
        };
        var getMiliseconds = function(week, day){
            return (week*7+day)*86400000;
        }
        return {
            menuToString: menuToString,
            addToCalendar: addToCalendar,
            getWeek:getWeek,
            getDay:getDay,
            getMiliseconds:getMiliseconds
        };
    });
