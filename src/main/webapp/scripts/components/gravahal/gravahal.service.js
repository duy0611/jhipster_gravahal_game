'use strict';

angular.module('gravahallApp')
    .factory('GravaHalService', function ($http) {
        return {
        	createNewGameForUser: function(loginname) {
        		return $http.post('api/gravaHalGames/createNewGame', loginname);
        	},
        	getUnfinishedGames: function() {
        		return $http.get('api/gravaHalGames/getUnfinishedGames');
        	},
        	joinTable: function(gameId, loginName) {
        		return $http.post('api/gravaHalGames/joinTable?gameId=' + gameId + '&loginName=' + loginName);
        	},
        	watchTable: function(gameId, loginName) {
        		
        	},
        	sowPit: function(gameId, loginName, pitIndex) {
        		return $http.post('api/gravaHalGames/sowPit?gameId=' + gameId + '&loginName=' + loginName + '&pitIndex=' + pitIndex);
        	}
        };
    });
