angular.module('chefsApp').directive('notFound', function()
{
    return {
        restrict: 'E',
        scope: {
            results:"=",
            message:"=",
            glyphicon:"="
        },
        template: '<div ng-if="results.length==0" class="text-center">' +
        '<h2 ng-if="!message"><span translate="global.search.notFound"></span></h2>' +
        '<h2 ng-if="message"><span translate="{{message}}"></span></h2>' +
        '<h1 ng-if="!glyphicon"><span class="glyphicon glyphicon-search"></span></h1>' +
        '<h1 ng-if="glyphicon"><span class="{{glyphicon}}"></span></h1>' +
        '</div>'
    }
});
