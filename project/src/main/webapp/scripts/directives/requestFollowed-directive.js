/**
 * Created by juanlu on 13/02/16.
 */

angular.module('chefsApp').directive('requestFollowed', ['RequestAPI',function()
{
    return {
        restrict: 'E',
        scope: {
            user:"="
        },
        controller: 'RequestFollowedDirectiveController',
        templateUrl: 'scripts/directives/requestFollowed-template.html'
    }
}]);
