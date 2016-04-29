'use strict';

angular.module('chefsApp')
    .controller('ChefActivityLog', function ($scope, $state, $stateParams, ActivityLog, ParseLinks) {
        $scope.activityLogs = [];
        $scope.page = 0;
        $scope.pageSize = 15;
        $scope.links = null;
        $scope.assessments = ['ASSESSMENT_RECIPE', 'ASSESSMENT_COMPETITION', 'ASSESSMENT_EVENT', 'ASSESSMENT'];
        var login = $stateParams.login;
        $scope.loadAll = function() {
            ActivityLog.activityLogsOfUser(login, {page: $scope.page, size: $scope.pageSize}).then(function(result) {
                $scope.links = ParseLinks.parse(result.headers('link'));
                for (var i = 0; i < result.data.length; i++) {
                    $scope.activityLogs.push(result.data[i]);
                }
            });
        };
        $scope.reset = function() {
            $scope.page = 0;
            $scope.activityLogs = [];
            $scope.loadAll();
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.refresh = function () {
            $scope.reset();
            $scope.clear();
        };

        $scope.calculateMoment = function(moment){
            var s = Math.floor((new Date() - moment)/( 1000 ));
            var m = Math.floor((new Date() - moment)/( 1000 * 60 ));
            var h = Math.floor((new Date() - moment)/( 1000 * 60 * 60 ));
            var d = Math.floor((new Date() - moment)/(  1000 * 60 * 60 * 24 ));
            var result;

            if( s < 60){
                result = s+'s';
            }else if(m < 60){
                result = m+'m';
            }else if(h < 24){
                result = h+'h';
            }else if(d < 10){
                result = d+'d';
            }else{
                result = moment.toLocaleDateString();
            }
            return result;
        };

        $scope.redirect = function(activityLog){
            $state.go('ChefRecipeDisplay', {login: activityLog.login, id: activityLog.objectId});
        };

    });
