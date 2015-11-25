'use strict';

angular.module('chefsApp').controller('RecipeEditController',
        function($rootScope, $scope, $stateParams, entity, Recipe) {

        $scope.step = {position: null, section: null, id: null, stepPicture:[]};
        $scope.recipe = entity;
        $scope.load = function(id) {
            Recipe.get({id : id}, function(result) {
                $scope.recipe = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('chefsApp:recipeUpdate', result);
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.recipe.id != null) {
                Recipe.update($scope.recipe, onSaveFinished);
            } else {
                Recipe.save($scope.recipe, onSaveFinished);
            }
        };

//Steps
        $scope.addStep = function (){
            $scope.recipe.steps.push(angular.copy($scope.step));
            $scope.step = {position: null, section: null, id: null, stepPicture:[]};
        };
        $scope.deleteStep = function(position){
            $scope.recipe.steps.splice(position,1);
        };
        $scope.sortableOptions = {
            stop: function(e, ui) {
                $scope.recipe.steps.forEach(function(element, index, array){element.position = index;});
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
        $scope.addImg = function(step) {
            step.stepPicture.push({title:null, src:null, properties:null});
        };
        $scope.deleteImg = function(step, img){
            $scope.recipe.steps[step].stepPicture.splice(img,1);
        };

//Pictures

        $scope.byteSize = function (base64String) {
            if (!angular.isString(base64String)) {
                return '';
            }
            function endsWith(suffix, str) {
                return str.indexOf(suffix, str.length - suffix.length) !== -1;
            }
            function paddingSize(base64String) {
                if (endsWith('==', base64String)) {
                    return 2;
                }
                if (endsWith('=', base64String)) {
                    return 1;
                }
                return 0;
            }
            function size(base64String) {
                return base64String.length / 4 * 3 - paddingSize(base64String);
            }
            function formatAsBytes(size) {
                return size.toString().replace(/\B(?=(\d{3})+(?!\d))/g, " ") + " bytes";
            }

            return formatAsBytes(size(base64String));
        };

        $scope.setSrc = function ($file, stepPicture) {
            if ($file && $file.$error == 'pattern') {
                return;
            }
            if ($file) {
                var fileReader = new FileReader();
                fileReader.readAsDataURL($file);
                fileReader.onload = function (e) {
                    var data = e.target.result;
                    var base64Data = data.substr(data.indexOf('base64,') + 'base64,'.length);
                    $scope.$apply(function() {
                        stepPicture.src = base64Data;
                    });
                };
            }
        };

        $scope.byteSize = function (base64String) {
            if (!angular.isString(base64String)) {
                return '';
            }
            function endsWith(suffix, str) {
                return str.indexOf(suffix, str.length - suffix.length) !== -1;
            }
            function paddingSize(base64String) {
                if (endsWith('==', base64String)) {
                    return 2;
                }
                if (endsWith('=', base64String)) {
                    return 1;
                }
                return 0;
            }
            function size(base64String) {
                return base64String.length / 4 * 3 - paddingSize(base64String);
            }
            function formatAsBytes(size) {
                return size.toString().replace(/\B(?=(\d{3})+(?!\d))/g, " ") + " bytes";
            }

            return formatAsBytes(size(base64String));
        };

        $scope.setSrc = function ($file, socialPicture) {
            if ($file && $file.$error == 'pattern') {
                return;
            }
            if ($file) {
                var fileReader = new FileReader();
                fileReader.readAsDataURL($file);
                fileReader.onload = function (e) {
                    var data = e.target.result;
                    var base64Data = data.substr(data.indexOf('base64,') + 'base64,'.length);
                    $scope.$apply(function() {
                        socialPicture.src = base64Data;
                    });
                };
            }
        };


        $rootScope.securityEntity = $scope.recipe;

});
