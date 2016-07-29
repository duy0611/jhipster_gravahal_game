angular.module('gravahallApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('tracker', {
                parent: 'admin',
                url: '/tracker',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'Real-time user activities'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/admin/tracker/tracker.html',
                        controller: 'TrackerController'
                    }
                },
                resolve: {
                    
                },
                onEnter: function(Tracker) {
                    Tracker.subscribe();
                },
                onExit: function(Tracker) {
                    Tracker.unsubscribe();
                },
            });
    });
