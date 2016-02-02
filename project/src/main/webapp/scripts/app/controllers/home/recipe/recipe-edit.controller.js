'use strict';

angular.module('chefsApp').controller('RecipeEditController',
        function($rootScope, $state, $scope, $stateParams, entity, Recipe, FoodSearch, TagByNameContains) {

        $scope.step = {position: null, section: null, id: null, stepPicture:[], ingredients:[]};
        $scope.recipe = entity;
        $scope.page = 0;
        $scope.foodsCache = {};
        $scope.listOfFoods = [];
        $scope.searchFood = function(ingredient) {
            if(ingredient.food.name != null && ingredient.food.name.length > 0){
                if($scope.foodsCache[ingredient.food.name] === undefined ){
                    FoodSearch.query({name : ingredient.food.name, page: $scope.page, size: 10}, function(result) {
                        $scope.foodsCache[ingredient.food.name] = result;
                        $scope.refreshListOfFoods(ingredient);
                    });
                }else{
                    $scope.refreshListOfFoods(ingredient);
                }
            }
        };
        $scope.refreshListOfFoods = function(ingredient){
            $scope.listOfFoods = $scope.foodsCache[ingredient.food.name].slice();
            ingredient.food.id = null;
            $scope.listOfFoods.forEach(function(element){
                if ( element.name == ingredient.food.name){
                    ingredient.food = element;
                }
            });
        };
        $scope.load = function(id) {
            Recipe.get({id : id}, function(result) {
                $scope.recipe = result;
            });
        };

        var onSaveFinished = function (result) {
            $state.go('HomeRecipesDisplay', {id:result.id, message:result});
        };

        $scope.save = function () {
            if ($scope.recipe.id != null) {
                Recipe.update($scope.recipe, onSaveFinished);
            } else {
                Recipe.save($scope.recipe, onSaveFinished);
            }
        };
//Tags

        $scope.loadTags = function(query) {
            return TagByNameContains.query({name:query}).$promise;
        };

//Add Steps to Recipe
        $scope.addStep = function (){
            $scope.recipe.steps.push(angular.copy($scope.step));
            $scope.step = {position: null, section: null, id: null, stepPicture:[], ingredients:[]};
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
//Add Picture to Step
        $scope.addImg = function(step) {
            step.stepPicture.push({title:null, src:null, properties:null, id:null});
        };
        $scope.deleteImg = function(step, img){
            step.stepPicture.splice(img,1);
        };
//Add Ingredient to Step
        $scope.addIngredient = function(step){
            step.ingredients.push({id:null, amount:null, measurement:null, food:{id:null, normalizaedName:null, name:'', kcal:null}})
        };
        $scope.deleteIngredient = function(step, ingredient){
            step.ingredients.splice(ingredient,1);
        };
//Pictures tools
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

        $rootScope.securityEntity = $scope.recipe;

});
