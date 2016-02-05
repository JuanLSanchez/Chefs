/**
 * Created by juanlu on 5/02/16.
 */

angular.module('chefsApp')
    .controller('ModalInstanceController', function ($scope, $state, $modalInstance, picture, pictures, site) {

    $scope.picture = picture;
    $scope.pictures = pictures;

    $scope.ok = function () {
        $modalInstance.close($scope.pictureDetails);
    };

    $scope.close = function() {
        $modalInstance.close();
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
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
        $modalInstance.close(function (){
            if (site == 'home'){
                $state.go('HomeRecipesDisplay',{id:pictures[picture].recipe});
            }else{
                $state.go('ChefRecipeDisplay',{id:pictures[picture].recipe, login:pictures[picture].user});
            }
        });
    };
});
