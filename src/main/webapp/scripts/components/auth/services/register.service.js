'use strict';

angular.module('gravahallApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


