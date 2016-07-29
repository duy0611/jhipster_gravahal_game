'use strict';

angular.module('gravahallApp')
    .controller('GravahalMainController', function ($location, $scope, Principal, GravaHalService) {
        Principal.identity().then(function(account) {
            $scope.account = account;
            $scope.isAuthenticated = Principal.isAuthenticated;
        });
        
        $scope.gravaHalGames = [];
        $scope.getUnfinishedGames = function() {
        	GravaHalService.getUnfinishedGames().success(function(results) {
        		//console.log(results);
        		$scope.gravaHalGames = results;
            });
        };
        $scope.getUnfinishedGames();
        
        $scope.createNewGame = function() {
        	GravaHalService.createNewGameForUser($scope.account.login).success(function (results) {       		
        		//console.log(results);
        		$location.path('/gravahal/play_game/' + results.id);     		
        	});
        };
        
        $scope.refresh = function() {
        	$scope.getUnfinishedGames();
        };
        
        $scope.joinTable = function(gameId, loginName) {
        	GravaHalService.joinTable(gameId, loginName).success(function(results) { 
        		//console.log(results);
        		$location.path('/gravahal/play_game/' + results.id);
        	});
        };
        
        $scope.watchTable = function(gameId, loginName) {
        	
        };
        
        $scope.playTable = function(gameId) {
        	$location.path('/gravahal/play_game/' + gameId);
        }
    });
