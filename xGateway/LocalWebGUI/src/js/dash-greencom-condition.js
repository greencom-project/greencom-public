'use strict';

// create the text-measure module
var dashGreencomCondition = angular.module('dashGreencomCondition',
		[ 'restangular' ]);
// the module controller
dashGreencomCondition
		.controller(
				'dashGreencomConditionController',
				[
						'$scope',
						'$element',
						'$attrs',
						'$timeout',
						'Restangular',
						function($scope, $element, $attrs, $timeout,
								Restangular) {
							var grid = 'sensor_meter_grid';
							var consumption = 'sensor_meter_mains';
							var production = 'sensor_meter_pv';
							var soc = 'sensor_battery_soc';
							var akku = 'sensor_meter_akku';

							$scope.consumption_value = 0;
							$scope.production_value = 0;
							$scope.soc = 0;
							$scope.grid_value = 0;
							$scope.akku_value = 0;

							$scope.result = '';
							function update() {
								$timeout(
										function() {
											var headerObj = {
												'accept' : 'application/json'
											};
											var devices = Restangular
													.all('sensors');
											devices
													.get(consumption, {},
															headerObj)
													.then(
															function(response) {
																$scope.consumption_value = parseFloat(response.result.sensor_value);
															});
											devices
													.get(production, {},
															headerObj)
													.then(
															function(response) {
																$scope.production_value = parseFloat(response.result.sensor_value);
															});
											devices
													.get(soc, {}, headerObj)
													.then(
															function(response) {
																$scope.soc = parseFloat(response.result.sensor_value);
															});
											devices
													.get(akku, {}, headerObj)
													.then(
															function(response) {
																$scope.akku_value = parseFloat(response.result.sensor_value);
															});
											devices
													.get(grid, {}, headerObj)
													.then(
															function(response) {
																$scope.grid_value = parseFloat(response.result.sensor_value);
															});

											if (($scope.production_value > $scope.consumption_value)
													&& ($scope.akku_value > 0)) {
												$scope.result = "Your PV production exceeds your consumption, so your battery is charging. Congratulations!"
												$scope.icon = 'pics/battery-charging.png'

											} else if (($scope.production_value == $scope.consumption_value)
													&& ($scope.akku_value == 0)) {
												$scope.result = "You are consuming as much as your are producing: Bingo!"
												$scope.icon = 'pics/save-money.png'

											} else if (($scope.production_value < $scope.consumption_value)
													&& ($scope.soc > 49)
													&& ($scope.akku_value < 0)) {
												$scope.result = "Your battery is currently sustaining your home with clean energy: congratulations!"
												$scope.icon = 'pics/battery-full.png'

											} else if (($scope.production_value <= $scope.consumption_value)
													&& ($scope.soc < 50)
													&& ($scope.soc > 9)
													&& ($scope.akku_value < 0)) {
												$scope.result = "You are using your battery: please to consume less to extend the duration of available charge"
												$scope.icon = 'pics/battery-half.png'

											} else if (($scope.production_value <= $scope.consumption_value)
													&& ($scope.soc < 10)
													&& ($scope.akku_value < 0)) {
												$scope.result = "You are using your battery, but you are running out of juice: try to consume less!"
												$scope.icon = 'pics/battery-low.png'

											} else if (($scope.grid_value == $scope.consumption_value)
													&& ($scope.akku_value == 0)) {
												$scope.result = "You are running on Grid power: less consumption means saving money! Please consume as little as you can."
												$scope.icon = 'pics/money-fly.png'

											} else if (($scope.grid_value < 0)) {
												$scope.result = "Congratulations! Your house is energy-positive in this moment. You are a power station!"
												$scope.icon = 'pics/gain-money.png'

											} else {
												$scope.result = "Consume less! Any saved kilowatthour is a gained kilowatthour!"

											}

											update();
										}, 4000);
							}

							update();

						} ]);

// define the text-measure component
dashGreencomCondition.component('dashGreencomCondition', {
	// the component template
	templateUrl : 'templates/dash-greencom-condition.html',
	bindings : {
		result : '<',
		icon : '<'
	},
	controller : dashGreencomConditionController
});

// the widget controller, actually does nothing as no data can be changed /
// modified
function dashGreencomConditionController($scope, $element, $attrs) {
	// sensor = $attrs.dashLabel;
}