/**
 * Created by juanlu on 11/02/16.
 */

angular.module('chefsApp').directive('requestFollower', ['RequestAPI',function()
{
    return {
        restrict: 'E',
        scope: {
            user:"="
        },
        controller: 'RequestFollowerDirectiveController',
        templateUrl: 'scripts/directives/requestFollower-template.html'
    }
}]);
