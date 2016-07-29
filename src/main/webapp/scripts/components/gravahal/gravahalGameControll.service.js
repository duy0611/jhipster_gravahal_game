'use strict';

angular.module('gravahallApp')
    .factory('GravahalGameControll', function ($rootScope, $cookies, $http, $q) {
        var stompClient = null;
        var subscriber = null;
        var listener = $q.defer();
        var connected = $q.defer();
        function sendActivity() {
            if (stompClient != null && stompClient.connected) {
                stompClient
                    .send('/gravahal/gameactivity',
                    {},
                    JSON.stringify({'gameId': 123}));
            }
        }
        return {
            connect: function () {
                //building absolute path so that websocket doesnt fail when deploying with a context path
                var loc = window.location;
                var url = '//' + loc.host + loc.pathname + 'websocket/gamestate';
                var socket = new SockJS(url);
                stompClient = Stomp.over(socket);
                var headers = {};
                headers['X-CSRF-TOKEN'] = $cookies[$http.defaults.xsrfCookieName];
                stompClient.connect(headers, function(frame) {
                    connected.resolve("success");
                });
            },
            subscribe: function() {
                connected.promise.then(function() {
                    subscriber = stompClient.subscribe("/gravahal/gamestate", function(data) {
                        listener.notify(JSON.parse(data.body));
                    });
                }, null, null);
            },
            unsubscribe: function() {
                if (subscriber != null) {
                    subscriber.unsubscribe();
                }
            },
            receive: function() {
                return listener.promise;
            },
            sendActivity: function () {
                if (stompClient != null) {
                    sendActivity();
                }
            },
            disconnect: function() {
                if (stompClient != null) {
                    stompClient.disconnect();
                    stompClient = null;
                }
            }
        };
    });
