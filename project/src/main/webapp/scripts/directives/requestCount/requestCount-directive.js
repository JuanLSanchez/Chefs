angular.module('chefsApp').directive('requestCount', ['SocialEntity',function()
{
    return {
        restrict: 'E',
        scope: {
            user:"=",
            redirect:"="
        },
        controller: 'RequestCountDirectiveController',
        templateUrl: 'scripts/directives/requestCount/requestCount-template.html'
    }
}]);
