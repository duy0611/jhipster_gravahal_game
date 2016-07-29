'use strict';

angular.module('gravahallApp')
    .controller('GravaHalGameController', function ($scope, GravaHalGame) {
        $scope.gravaHalGames = [];
        $scope.loadAll = function() {
            GravaHalGame.query(function(result) {
               $scope.gravaHalGames = result;
            });
        };
        $scope.loadAll();

        $scope.delete = function (id) {
            GravaHalGame.get({id: id}, function(result) {
                $scope.gravaHalGame = result;
                $('#deleteGravaHalGameConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            GravaHalGame.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteGravaHalGameConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.gravaHalGame = {playerOne: null, playerTwo: null, nextTurn: null, id: null};
        };
    });
