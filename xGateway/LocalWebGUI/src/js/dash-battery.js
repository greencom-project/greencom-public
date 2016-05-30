'use strict';
var dashBattery = angular.module('dashBattery',
		[ 'ui.bootstrap', 'restangular' ]);

dashBattery
		.controller(
				"dashBatteryController",
				[
						'$scope',
						'$timeout',
						'Restangular',
						function($scope, $timeout, Restangular) {
							$scope.value = 0;
							// This controller update automatically the gauge
							// based on an API
							// The alternative is to feed the gauge from an
							// external controller
							// in that case this method can be removed

							function update() {
								$timeout(
										function() {
											var headerObj = {
												'accept' : 'application/json'
											};
											var devices = Restangular
													.all('sensors');
											devices
													.get("sensor_battery_soc",
															{}, headerObj)
													.then(
															function(response) {
																// $scope.value
																// =
																// device.status.SinglePhaseActivePowerMeasurementState[0].value.split('
																// ')[0];
																$scope.value = parseFloat(response.result.sensor_value);
																findType($scope.value)
															});
											update();
										}, 2000);
							}

							update();

							$scope.type = 'success';
							function findType(value) {
								switch (true) {
								case (0 <= $scope.value && $scope.value < 10):
									$scope.type = 'danger'
									break;
								case (10 <= $scope.value && $scope.value < 30):
									$scope.type = 'warning'
									break;
								case (30 <= $scope.value && $scope.value < 70):
									$scope.type = 'info'
									break;
								case (70 <= $scope.value && $scope.value < 100):
									$scope.type = 'success'
									break;
								default:
									$scope.type = 'success'
									break;
								}
							}

						} ]);

// define the switch component
dashBattery.component('dashBattery', {
	// the component template
	templateUrl : 'templates/dash-battery.html',
	bindings : {
		value : '<',
		dashUnit : '<',
		type : '<',
		dashTextColor : '<'
	},
	controller : dashBatteryController
});

function dashBatteryController() {
}

dashBattery.config(function(RestangularProvider) {
	RestangularProvider.setBaseUrl('http://localhost:8080/api/');
});