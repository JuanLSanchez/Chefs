/**
 * Created by juanlu on 11/02/16.
 */

angular.module('chefsApp').directive('like', ['Like',function()
{
    return {
        restrict: 'E',
        scope: {
            socialEntityId:"="
        },
        controller: 'LikesDirectiveController',
        templateUrl: 'scripts/directives/likes/likes-template.html'
    }
}]);
