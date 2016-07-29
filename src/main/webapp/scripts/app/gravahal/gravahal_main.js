'use strict';

angular.module('gravahallApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('gravahal', {
                parent: 'site',
                url: '/gravahal',
                data: {
                    roles: ['ROLE_USER']
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/gravahal/gravahal_main.html',
                        controller: 'GravahalMainController'
                    }
                },
                resolve: {
                    
                }
            })
            .state('gravahal.play_game', {
                parent: 'site',
                url: '/gravahal/play_game/{id}',
                data: {
                    roles: ['ROLE_USER']
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/gravahal/gravahal_play_game.html',
                        controller: 'GravahalPlayGameController'
                    }
                },
                resolve: {
                    
                },
                onEnter: function($stateParams, Principal, GravahalGameControll) {
                	GravahalGameControll.subscribe();
                },
                onExit: function($stateParams, Principal, GravahalGameControll) {
                	GravahalGameControll.unsubscribe();
                },
            });
    });
