/**
 * Created by juanlu on 11/02/16.
 */

angular.module('chefsApp').directive('comments', ['Comment',function()
{
    return {
        restrict: 'E',
        scope: {
            socialEntityId:"="
        },
        controller: 'CommentsDirectiveController',
        templateUrl: 'scripts/directives/comments/comments-template.html'
    }
}]);
