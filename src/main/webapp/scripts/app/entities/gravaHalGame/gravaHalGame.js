'use strict';

angular.module('gravahallApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('gravaHalGame', {
                parent: 'entity',
                url: '/gravaHalGames',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'GravaHalGames'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/gravaHalGame/gravaHalGames.html',
                        controller: 'GravaHalGameController'
                    }
                },
                resolve: {
                }
            })
            .state('gravaHalGame.detail', {
                parent: 'entity',
                url: '/gravaHalGame/{id}',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'GravaHalGame'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/gravaHalGame/gravaHalGame-detail.html',
                        controller: 'GravaHalGameDetailController'
                    }
                },
                resolve: {
                    entity: ['$stateParams', 'GravaHalGame', function($stateParams, GravaHalGame) {
                        return GravaHalGame.get({id : $stateParams.id});
                    }]
                }
            })
            .state('gravaHalGame.new', {
                parent: 'gravaHalGame',
                url: '/new',
                data: {
                    roles: ['ROLE_ADMIN'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/gravaHalGame/gravaHalGame-dialog.html',
                        controller: 'GravaHalGameDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {playerOne: null, playerTwo: null, nextTurn: null, id: null};
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('gravaHalGame', null, { reload: true });
                    }, function() {
                        $state.go('gravaHalGame');
                    })
                }]
            })
            .state('gravaHalGame.edit', {
                parent: 'gravaHalGame',
                url: '/{id}/edit',
                data: {
                    roles: ['ROLE_ADMIN'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/gravaHalGame/gravaHalGame-dialog.html',
                        controller: 'GravaHalGameDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['GravaHalGame', function(GravaHalGame) {
                                return GravaHalGame.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('gravaHalGame', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
