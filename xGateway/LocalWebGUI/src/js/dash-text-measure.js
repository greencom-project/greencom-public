'use strict';

// create the text-measure module
var dashTextMeasure = angular.module('dashTextMeasure', [ 'restangular' ]);
// the module controller
dashTextMeasure
		.controller(
				'dashTextMeasureController',
				[
						'$scope',
						'$element',
						'$attrs',
						'$timeout',
						'Restangular',
						function($scope, $element, $attrs, $timeout,
								Restangular) {

							$scope.value = 0;
							$scope.greencom_text = '';
							// This controller update automatically the gauge
							// based on an API
							// The alternative is to feed the gauge from an
							// external controller
							// in that case this method can be removed
							$scope.init = function(name) {
								$scope.sensor = name;
							};

							function update() {
								$timeout(
										function() {
											var headerObj = {
												'accept' : 'application/json'
											};
											var devices = Restangular
													.all('sensors');
											devices
													.get($scope.sensor, {},
															headerObj)
													.then(
															function(response) {
																// $scope.value
																// =
																// device.status.SinglePhaseActivePowerMeasurementState[0].value.split('
																// ')[0];
																$scope.value = parseFloat(response.result.sensor_value);

																// case for
																// different
																// sensors and
																// conditions
																// for them
																switch ($scope.sensor) {
																case 'sensor_meter_grid':
																	if ($scope.value == 0) {
																		$scope.greencom_text = 'You are not exchanging energy';
																	} else if ($scope.value > 0) {
																		$scope.greencom_text = 'You are purchasing energy';
																	} else {
																		$scope.greencom_text = 'You are selling energy';
																	}
																	break;
																case 'sensor_meter_mains':
																	if ($scope.value == 0) {
																		$scope.greencom_text = 'Zero consumption';
																	} else if ($scope.value > 800) {
																		$scope.greencom_text = 'High consumption';
																	} else if ($scope.value > 400) {
																		$scope.greencom_text = 'Normal consumption';
																	} else {
																		$scope.greencom_text = 'Low consumption';
																	}
																	break;
																case 'sensor_meter_pv':
																	if ($scope.value == 0) {
																		$scope.greencom_text = 'Zero production';
																	} else if ($scope.value > 2000) {
																		$scope.greencom_text = 'High production';
																	} else if ($scope.value > 1000) {
																		$scope.greencom_text = 'Medium production';
																	} else {
																		$scope.greencom_text = 'Low production';
																	}
																	break;
																case 'sensor_meter_akku':
																	if ($scope.value == 0) {
																		$scope.greencom_text = 'Battery is not active';
																	} else if ($scope.value > 0) {
																		$scope.greencom_text = 'Battery discharging';
																	} else {
																		$scope.greencom_text = 'Battery charging';
																	}
																	break;
																default:
																	$scope.greencom_text = ''
																}

															});
											update();
										}, 5000);
							}

							update();

						} ]);

// define the text-measure component
dashTextMeasure.component('dashTextMeasure', {
	// the component template
	templateUrl : 'templates/dash-text-measure.html',
	bindings : {
		dashId : '<',
		value : '<',
		dashUnit : '<',
		dashIcon : '<',
		dashLabel : '<'
	},
	controller : dashTextMeasureController
});

// the widget controller, actually does nothing as no data can be changed /
// modified
function dashTextMeasureController($scope, $element, $attrs) {
	// sensor = $attrs.dashLabel;
}