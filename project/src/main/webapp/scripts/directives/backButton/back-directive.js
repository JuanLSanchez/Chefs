/**
 * Created by juanlu on 2/12/15.
 */

angular.module('chefsApp').directive('backButton', function()
{
    return {
        restrict: 'E',
        templateUrl: 'scripts/directives/backButton/back-button-template.html'
    }
});
