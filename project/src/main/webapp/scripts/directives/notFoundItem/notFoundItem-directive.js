angular.module('chefsApp').directive('notFoundItem', function()
{
    return {
        restrict: 'E',
        template: '<div class="text-center">' +
        '<h2><span translate="global.search.notFoundItem"></span></h2>' +
        '<h1><span class="glyphicon glyphicon-ban-circle"></span></h1>' +
        '</div>'
    }
});
