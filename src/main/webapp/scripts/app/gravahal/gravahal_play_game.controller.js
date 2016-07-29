'use strict';

angular.module('gravahallApp')
    .controller('GravahalPlayGameController', function ($scope, $stateParams, Principal, GravaHalGame, GravaHalService, GravahalGameControll) {
        Principal.identity().then(function(account) {
            $scope.account = account;
            $scope.isAuthenticated = Principal.isAuthenticated;
        });
        
        $scope.gravaHalGame = {};
        loadGravaHalGame($stateParams.id);
        
        GravahalGameControll.receive().then(null, null, function(gameActivity) {
            if(gameActivity.gameId == $scope.gravaHalGame.id) {
            	console.log('Received new game activity: ' + gameActivity);
            	//$scope.gravaHalGame.gameState = gameActivity.gameState;
            	//$scope.gravaHalGame.currentTurn = gameActivity.currentTurn;
            	loadGravaHalGame(gameActivity.gameId);
            }
        });
        
        $scope.testSendActivity = function() {
        	GravahalGameControll.sendActivity();
        };
        
        $scope.isUserTurn = function() {
        	if($scope.account.login == $scope.gravaHalGame.playerOne) {
        		return $scope.gravaHalGame.currentTurn == 'PLAYER_ONE';
        	}
        	else if($scope.account.login == $scope.gravaHalGame.playerTwo) {
        		return $scope.gravaHalGame.currentTurn == 'PLAYER_TWO';
        	}
        	console.log("Invalid game state: " + $scope.gravaHalGame.id);
        	return false; // invalid game state
        };
        
        $scope.sowPit = function(gameId, loginName, pitIndex) {
        	GravaHalService.sowPit(gameId, loginName, pitIndex).success(function (results) { 
        		loadGravaHalGame(gameId);
        	});
        };
        
        function loadGravaHalGame(id) {
        	GravaHalGame.get({id: id}, function(result) {
            	//console.log(result);
                $scope.gravaHalGame = result;
            });
        }
    });
