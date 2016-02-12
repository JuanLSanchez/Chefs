/**
 * Created by juanlu on 11/02/16.
 */

angular.module('chefsApp').directive('requestCount', ['RequestAPI',function()
{
    return {
        restrict: 'E',
        scope: {
            user:"=",
            redirect:"="
        },
        controller: 'RequestCountDirectiveController',
        templateUrl: 'scripts/directives/requestCount-template.html'
    }
}]);
