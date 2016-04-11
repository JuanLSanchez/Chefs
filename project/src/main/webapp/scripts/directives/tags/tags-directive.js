/**
 * Created by juanlu on 2/12/15.
 */

angular.module('chefsApp').directive('tags', function()
{
    return {
        restrict: 'A',
        templateUrl: 'scripts/directives/tags/tags-template.html',
        scope: {
            tags:"=tagsList"
        }
    }
});
