'use strict';
var dashDeviceSwitch = angular.module('dashDeviceSwitch',
		[ 'frapontillo.bootstrap-switch' ]);

// the module controller
dashDeviceSwitch.controller('dashDeviceSwitchController', function($scope) {

	$scope.state = true;

});

// define the switch component
dashDeviceSwitch.component('dashDeviceSwitch', {
	// the component template
	templateUrl : 'templates/dash-device-switch.html',
	bindings : {
		dashState : '<',
		dashLabel : '<',
		// callback
		onChanged : '&'

	},
	controller : dashDeviceSwitchController
});

// the widget controller, actually does nothing as no data can be changed /
// modified
function dashDeviceSwitchController($scope, $element, $attrs) {
	var ctrl = this;
	ctrl.changedState = function() {

		console.log("device atatus changes")
	}
};