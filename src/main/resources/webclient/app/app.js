/**
 * 
 */
(function(){
	
	'use strict';
	angular.module('app', []);
	
	angular.module('app').controller('navCtrl', function($scope) {
		$scope.info = 'Directive Info v0.1';
	});
	
	angular.module('app').factory('WsSocketService',  ['$http', '$log', '$rootScope',  function($http, $log, $rootScope) {
		var wsocket = null;
		var url = 'ws://localhost:8090';
		
		var onMessageReceived = function(message) {
			var obj = angular.fromJson(message.data);
			$rootScope.$broadcast('wsmessage', obj);
		};
		
		var onOpen = function() {
			console.log('onOpen');
			$rootScope.$broadcast('wsopen', {state: 'connected'});
		};
		
		var onError = function() {
			console.log('onError');
		};

		var onClose = function() {
			console.log('onClose');
			$rootScope.$broadcast('wsopen', {state: 'disconnected'});
		};
		
		var sendData = function(data) {
			wsocket.send(data);
		};

		var closeWS = function() {
			wsocket.close();
		};
		
		var startWS = function() {
			console.log('start ws');
			
			wsocket = new WebSocket(url);
			
			wsocket.onmessage = onMessageReceived;
			wsocket.onopen = onOpen;
			wsocket.onerror = onError;
			wsocket.onclose = onClose;
			
		};
		
		return {
			startWS : startWS,
			closeWS: closeWS,
			sendData: sendData
		}
	}]);
	
	angular.module('app').directive('info', ['$http', 'WsSocketService',  function($http, WsSocketService) {
		return {
		    restrict: 'EA',
		    scope: {
		      title: '@' //isolated scope
		    },
		    templateUrl: 'template/template.html',
		    link: function (scope, element) {
		        scope.info = 'Directive Info v0.1';
		        scope.wsstate = 'disconnected';
				scope.restData = '';
				scope.disabled = null;
		        
		        scope.clickHandler = function() {
					$http.get('/rest/name').success(function(data, status, headers, config) {
						console.log('rest call succeded ' + angular.toJson(data));
						scope.restData = data;
						console.log(scope.restData.name);
					}).
					error(function(data, status, headers, config) {
						console.log('error');
					});
				};

				scope.getWsState = function () {
					if (scope.disabled === null) {
						return '';
					}

					if (!scope.disabled) {
						return 'alert alert-success';
					}
					else {
						return 'alert alert-danger';
					}
				};

				scope.startWS = function() {
					WsSocketService.startWS();
					scope.disabled = false;
				};

				scope.closeWS = function() {
					WsSocketService.closeWS();
					scope.disabled = true;
				};
				
				scope.sendWS = function() {
					WsSocketService.sendData('data from client');
				};
				
				scope.$on('wsmessage', function(message, data) {
					console.log('ws message ' +message + ' status ' +data.data);
					scope.data = data.data;
					scope.$apply();
				});
				
				scope.$on('wsopen', function(message, data) {
					scope.wsstate = data.state;
					scope.$apply();
				});
		    }
		};
	}]);

})();