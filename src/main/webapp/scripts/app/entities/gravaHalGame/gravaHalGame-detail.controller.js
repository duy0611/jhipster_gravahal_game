'use strict';

angular.module('gravahallApp')
    .controller('GravaHalGameDetailController', function ($scope, $rootScope, $stateParams, entity, GravaHalGame) {
        $scope.gravaHalGame = entity;
        $scope.load = function (id) {
            GravaHalGame.get({id: id}, function(result) {
                $scope.gravaHalGame = result;
            });
        };
        $rootScope.$on('gravahallApp:gravaHalGameUpdate', function(event, result) {
            $scope.gravaHalGame = result;
        });
    });
