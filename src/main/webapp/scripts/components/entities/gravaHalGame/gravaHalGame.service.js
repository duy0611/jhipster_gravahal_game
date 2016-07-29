'use strict';

angular.module('gravahallApp')
    .factory('GravaHalGame', function ($resource, DateUtils) {
        return $resource('api/gravaHalGames/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    });

