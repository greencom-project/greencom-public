'use strict';

// create the text-measure module
var dashClockDayWidget = angular.module('dashClockDay', [ 'ds.clock' ]);

// the module controller
dashClockDayWidget.controller('mainCtrl', function($scope) {
});

// the widget controller
function dashClockDayController() {
	var ctrl = this;
	console.log(ctrl.showAnalog);
	if (typeof ctrl.showAnalog === 'undefined'
			|| typeof ctrl.showDigital === 'undefined') {
		if (typeof ctrl.showAnalog === 'undefined')
			ctrl.showAnalog = 'true';
		if (typeof ctrl.showDigital === 'undefined') {
			ctrl.showDigital = 'true';
		}
	} else if (ctrl.showDigital == 'false' && ctrl.showAnalog == 'false') {
		ctrl.showDigital = 'true';
		ctrl.showAnalog = 'true';
	} else {
		// doing nothing
	}

	console.log("From component controller");
	console.log("analog: " + ctrl.showAnalog);
	console.log("digital: " + ctrl.showDigital);

	// console.log("From $scope");
	// console.log(ctrl.showAnalog);
	// console.log(ctrl.showAnalog);
}
// define the clock-day component
dashClockDayWidget.component('dashClockDay', {
	// the component template
	templateUrl : 'templates/dash-clock-day.html',
	bindings : {
		digitalFormat : '<',
		theme : '<',
		showDigital : '<',
		showAnalog : '<'
	},
	controller : dashClockDayController
});
