/**
 * Created by juanlu on 5/02/16.
 */

angular.module('chefsApp')
    .controller('ModalInstanceController', function ($scope, $state, $uibModalInstance, picture, pictures, site) {

    $scope.picture = picture;
    $scope.pictures = pictures;

    $scope.ok = function () {
        $uibModalInstance.close($scope.pictureDetails);
    };

    $scope.close = function() {
        $uibModalInstance.close();
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };

    $scope.nextPicture = function(){
        if ($scope.picture < $scope.pictures.length - 1 ){
            $scope.picture = $scope.picture + 1;
        }
    };

    $scope.prevPicture = function(){
        if ($scope.picture > 0 ){
            $scope.picture = $scope.picture - 1;
        }
    };

    $scope.displayRecipe = function(){
        $uibModalInstance.close(function (){
            if (site == 'home'){
                $state.go('HomeRecipesDisplay',{id:pictures[picture].recipe});
            }else{
                $state.go('ChefRecipeDisplay',{id:pictures[picture].recipe, login:pictures[picture].user});
            }
        });
    };
});
