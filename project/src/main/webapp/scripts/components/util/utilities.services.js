'use strict';

angular.module('chefsApp')
    .service('UtilitiesServices', function () {
        var objects = {};

        var addObject = function(newObjName, newObj) {
            objects[newObjName] = newObj;
        };

        var getObject = function(ObjName){
            return objects[ObjName];
        };

        return {
            addObject: addObject,
            getObject: getObject
        };
    });
angular.module('chefsApp')
    .service('RecipeScope', function () {
        return {};
    });
