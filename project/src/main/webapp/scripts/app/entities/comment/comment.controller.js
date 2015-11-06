'use strict';

angular.module('chefsApp')
    .controller('CommentController', function ($scope, Comment, ParseLinks) {
        $scope.comments = [];
        $scope.page = 0;
        $scope.loadAll = function() {
            Comment.query({page: $scope.page, size: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                for (var i = 0; i < result.length; i++) {
                    $scope.comments.push(result[i]);
                }
            });
        };
        $scope.reset = function() {
            $scope.page = 0;
            $scope.comments = [];
            $scope.loadAll();
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.delete = function (id) {
            Comment.get({id: id}, function(result) {
                $scope.comment = result;
                $('#deleteCommentConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Comment.delete({id: id},
                function () {
                    $scope.reset();
                    $('#deleteCommentConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.reset();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.comment = {creationMoment: null, body: null, id: null};
        };
    });
