'use strict';

angular.module('gravahallApp').controller('GravaHalGameDialogController',
    ['$scope', '$stateParams', '$modalInstance', 'entity', 'GravaHalGame',
        function($scope, $stateParams, $modalInstance, entity, GravaHalGame) {

        $scope.gravaHalGame = entity;
        $scope.load = function(id) {
            GravaHalGame.get({id : id}, function(result) {
                $scope.gravaHalGame = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('gravahallApp:gravaHalGameUpdate', result);
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.gravaHalGame.id != null) {
                GravaHalGame.update($scope.gravaHalGame, onSaveFinished);
            } else {
                GravaHalGame.save($scope.gravaHalGame, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
}]);
