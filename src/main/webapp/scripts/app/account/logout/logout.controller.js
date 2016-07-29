'use strict';

angular.module('gravahallApp')
    .controller('LogoutController', function (Auth) {
        Auth.logout();
    });
