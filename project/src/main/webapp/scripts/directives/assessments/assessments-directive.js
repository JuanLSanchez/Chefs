/**
 * Created by juanlu on 11/02/16.
 */

angular.module('chefsApp').directive('assessments', ['Assessment',function()
{
    return {
        restrict: 'E',
        scope: {
            socialEntityId:"="
        },
        controller: 'AssessmentsDirectiveController',
        templateUrl: 'scripts/directives/assessments/assessments-template.html'
    }
}]);
