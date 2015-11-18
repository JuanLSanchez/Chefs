'use strict';

angular.module('chefsApp').controller('RecipeSecurityController'
    ,function($rootScope, $scope) {

        if ($rootScope.securityEntity.socialEntity.isPublic){
            $('input[name="checkbox-isPublic"]').attr('checked', '');
        }

        $(".switch-button").bootstrapSwitch();

        $scope.setIsPublic = function(value){
            $rootScope.securityEntity.socialEntity.isPublic = value;
        };
        $scope.setPublicInscription = function(value){
            $rootScope.securityEntity.socialEntity.publicInscription = value;
        };
        $scope.setBlocked = function(value){
            $rootScope.securityEntity.socialEntity.blocked = value;
        };

        $('input[name="checkbox-isPublic"]').on('switchChange.bootstrapSwitch', function(event, state) {
            $scope.setIsPublic(state);
        });
        $('input[name="checkbox-publicInscription"]').on('switchChange.bootstrapSwitch', function(event, state) {
            $scope.setPublicInscription(state);
        });
        $('input[name="checkbox-blocked"]').on('switchChange.bootstrapSwitch', function(event, state) {
            $scope.setBlocked(state);
        });

});
