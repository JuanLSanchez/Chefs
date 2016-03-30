'use strict';

angular.module('chefsApp').controller('MenuModalEditController',
    function ($scope, $state, $uibModalInstance, menu, calendar, oldMenu, Search, CalendarUtilities, Menu, schedule) {
        $scope.calendar = calendar;
        $scope.menu = menu;
        $scope.oldMenu = oldMenu;
        $scope.search = null;
        $scope.searchResult = [];
        $scope.page = 0;
        $scope.pageSize = 10;
        $scope.recipesIdToRemove = [];
        $scope.schedule = schedule;

        $scope.close = function () {
            $uibModalInstance.close(function () {
                if ($scope.oldMenu != null) {
                    restartToOldMenu(oldMenu);
                }
                return $scope.menu;
            });
        };

        $scope.confirmMenu = function () {
            var recipes = $scope.menu.recipes;
            $scope.menu.recipes = [];
            if($scope.menu.id!=null){
                Menu.update($scope.schedule.id, menu).then(function(result){
                    $scope.menu = result.data;
                    addRecipes(result.data, recipes);
                });
            }else{
                Menu.save($scope.schedule.id, menu).then(function(result){
                    $scope.menu = result.data;
                    addToCalendar($scope.menu, $scope.calendar);
                    addRecipes(result.data, recipes);
                });
            }
        };

        var addRecipes = function(menu, recipes){
            for(var recipeId in recipes){
                if($scope.menu.recipes[recipeId]==null){
                    addRecipe(recipeId, recipes[recipeId], menu);
                }
            }
            $scope.recipesIdToRemove.forEach(function(recipeId){
                Menu.removeRecipe(menu.id, recipeId);
            });
            $uibModalInstance.close(function () {
                return $scope.menu;
            });
        };

        var addRecipe = function(recipeId, recipeData, menu){
            Menu.addRecipe(menu.id, recipeId).then(function(){
                $scope.menu.recipes[recipeId] = [recipeData[0], recipeData[1]];
            });
        };

        var restartToOldMenu = function(oldMenu){
            $scope.menu.id = oldMenu.id;
            $scope.menu.time = oldMenu.time;
            $scope.menu.recipes = oldMenu.recipes;
        };

        /* Add menu to calendar */
        var addToCalendar = CalendarUtilities.addToCalendar;
        /* Functions to the days */
        var getWeek = CalendarUtilities.getWeek;
        var getDay = CalendarUtilities.getDay;
        var getMiliseconds = CalendarUtilities.getMiliseconds;

        /* Search and add recipe */

        $scope.addRecipe = function () {
            var recipe = $scope.searchResult[0];
            saveRecipe(recipe.firstField, recipe.secondField, recipe.login);
            $scope.search = null;
        };

        $scope.goSearch = function (search) {
            var recipe = search;
            saveRecipe(recipe.firstField, recipe.secondField, recipe.login);
            $scope.search = null;
        };

        var saveRecipe = function(recipeId, recipeName, login) {
            $scope.menu.recipes[recipeId] = [recipeName, login];
            if ($scope.menu.id != null && $scope.recipesIdToRemove != null) {
                var indexOfRemove = $scope.recipesIdToRemove.indexOf(recipeId);
            }
            if (indexOfRemove != null && indexOfRemove > -1) {
                $scope.recipesToRemove.splice(indexOfRemove, 1);
            }

        };

        $scope.prev = function () {
            $scope.searchResult.clear;
            var canSearch = $scope.search != null && $scope.search.length > 2;
            if (canSearch) {
                Search.recipes($scope.search.substr(1), {
                    page: $scope.page,
                    size: $scope.pageSize
                }).then(function (result) {
                    $scope.searchResult = result;
                });
            }
        };

        /* Remove recipe */
        $scope.removeRecipe = function (recipeId) {
            if (menu.id != null) {
                $scope.recipesIdToRemove.push(recipeId);
            }
            delete menu.recipes[recipeId];
        };
    });
