angular.module('chefsApp').directive('notFound', function()
{
    return {
        restrict: 'E',
        scope: {
            results:"="
        },
        template: '<div ng-if="results.length==0" class="text-center">' +
        '<h2><span translate="global.search.notFound"></span></h2>' +
        '<h1><span class="glyphicon glyphicon-search"></span></h1>' +
        '</div>'
    }
});
