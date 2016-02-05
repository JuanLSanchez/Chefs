'use strict';

angular.module('chefsApp')
    .controller('RecipePicturesController', function ($state, $timeout, $scope, $stateParams, RecipeUser,
                                                      ParseLinks, $modal, site) {
        $scope.pictures = [];
        $scope.pictureDetails = {};
        $scope.pictureIndex = 0;
        $scope.page = 0;
        $scope.loadAll = function() {
            RecipeUser.get({login:$stateParams.login ,page: $scope.page, size: 10}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                for (var i = 0; i < result.length; i++) {
                    if(result[i].socialEntity.socialPicture.src != null) {
                        $scope.pictures.push({
                            src: result[i].socialEntity.socialPicture.src, recipe: result[i].id,
                            recipeName: result[i].name, title: result[i].socialEntity.socialPicture.title,
                            updateDate: result[i].updateDate, class: 'col-xs-4', user: result[i].user.login
                        });
                    }
                    for (var j = 0; j < result[i].steps.length; j++ ){
                        for (var k = 0; k < result[i].steps[j].stepPicture.length; k++){
                            if(result[i].steps[j].stepPicture[k].src != null) {
                                $scope.pictures.push({
                                    src: result[i].steps[j].stepPicture[k].src, recipe: result[i].id,
                                    recipeName: result[i].name, title: result[i].steps[j].stepPicture[k].title,
                                    updateDate: result[i].updateDate, class: 'col-xs-4', user: result[i].user.login
                                });
                            }
                        }
                    }
                }
            });
        };
        $scope.reset = function() {
            $scope.page = 0;
            $scope.pictures = [];
            $scope.loadAll();
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.refresh = function () {
            $scope.reset();
            $scope.clear();
        };


        $scope.open = function (picture) {

            $modal.open({
                templateUrl: 'scripts/templates/picture.html',
                controller: 'ModalInstanceController',
                size: 'lg',
                resolve: {
                    pictures: function () { return $scope.pictures; },
                    picture:  function () { return picture; },
                    site: function() { return site; }
                }
            }).result.then(function(result) {
                    result();
            });
        };

    });
