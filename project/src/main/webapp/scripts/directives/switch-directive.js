/**
 * Created by juanlu on 20/11/15.
 */
angular.module('chefsApp').directive('switch', function()
{
    return {
        restrict: 'E',
        templateUrl: 'scripts/directives/switch-template.html',
        scope: {
            id:'=',
            object:'=',
            tag:'='
        }
    }
});
