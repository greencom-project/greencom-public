'use strict';

/*
 * Host angular app, in the long term this will just contain a list of modules
 * on which the app depends, i.e., of widgets used by the app.
 */

var app = angular.module('app', [ 'ngRoute', 'restangular', 'dashNavigation',
		'dashWeatherbig', 'dashWeathersmall', 'dashTextMeasure', 'dashBattery',
		'dashDeviceSwitch', 'dashHistoricTimeSeries', 'dashClockDay',
		'dashGreencomCondition', 'frapontillo.bootstrap-switch' ]);

// declare the routes for showing the views
app.config([ '$routeProvider', function($routeProvider) {
	$routeProvider.when('/home', {
		templateUrl : 'views/home.html'
	});
	$routeProvider.when('/graphs', {
		templateUrl : 'views/graphs.html'
	});
	$routeProvider.when('/control', {
		templateUrl : 'views/control.html'
	});
	$routeProvider.otherwise({
		redirectTo : '/home'
	});
} ]);

// the app controller for mainly historic chart
app.controller('greencomCtrl', // controller name
[
		'$scope',
		'Restangular',
		'$attrs',
		function($scope, Restangular, $attrs) {
			// a more easy to use reference to this controller avoiding scope
			// confusion
			var ctrl = this;

			var sensor = $attrs.sensor
			// define the initial range for data to be loaded as the last
			// month

			// end date will be today
			var today = new Date();
			// start date will be one month ago
			var oneDayAgo = new Date();
			oneDayAgo.setDate(oneDayAgo.getDate() - 1);

			var oneHourAgo = new Date();
			oneHourAgo.setHours(oneHourAgo.getHours() - 25);

			ctrl.observations = Restangular.all('sensors');

			// prepare the cache for fill levels gathered from observations

			// fill chart data with temporary dummy data to avoid issues
			// related to chart generation with no data defined, e.g., wrong
			// axis computation. It will be deleted upon data loading
			$scope.fillLevels = [ {
				values : [ {
					x : oneDayAgo,
					y : 0.0
				}, {
					x : today,
					y : 0.0
				} ],
				key : 'no data',
				color : '#0000ff'
			} ];

			ctrl.getObservations = function(sensor, startDate, endDate) {
				var headerObj = {
					'accept' : 'application/json'
				};
				var values = [];
				ctrl.observations.get(
						sensor + "/historic?from=" + startDate + "&to="
								+ endDate, {}, headerObj)
						.then(
								function(response) {
									// iterate over results
									angular.forEach(response.result, function(
											object) {
										// check that the observation value is
										// valid
										if (object != "NaN") {
											// store the value in the data
											// series cache
											// console.log (timeString)
											var date = new Date(
													object.timestamp)
											ctrl.fillLevels.values.push({
												x : date,
												y : object.value
											});
										}
									});

									$scope.fillLevels = [ angular
											.copy(ctrl.fillLevels) ];

								});
				// choosing color of the line chart base on the parameter
				var color_selected;
				if (sensor == "sensor_meter_grid") {
					color_selected = '#df3939';
				} else if (sensor == "sensor_meter_mains") {
					color_selected = '#ff5500';
				} else if (sensor == "sensor_meter_pv") {
					color_selected = '#3cab5f';
				} else {
					color_selected = '#0000ff';
				}

				ctrl.fillLevels = {
					values : values,
					key : sensor,
					color : color_selected
				};
			};

			/**
			 * Handler to component triggered data range changes
			 */
			ctrl.onUpdateRange = function(sensor, startDate, endDate) {
				// get local time
				var tzoffset = (new Date()).getTimezoneOffset() * 60000; // offset
																			// in
																			// milliseconds
				var startDate_localISOTime = (new Date(startDate - tzoffset))
						.toISOString();
				var endDate_localISOTime = (new Date(endDate - tzoffset))
						.toISOString();
				// get observations within the given start-end date range
				ctrl.getObservations(sensor, startDate_localISOTime,
						endDate_localISOTime);
			};

			// initial data
			// get ISO local time
			var tzoffset = (new Date()).getTimezoneOffset() * 60000; // offset
																		// in
																		// milliseconds
			var oneHourAgo_localISOTime = (new Date(oneHourAgo - tzoffset))
					.toISOString();
			var now_localISOTime = (new Date(today - tzoffset)).toISOString();

			ctrl.getObservations(sensor, oneHourAgo_localISOTime,
					now_localISOTime);

		} ]);


// base URL to get data from API, by changing this, all URLs will be changed
app.config(function(RestangularProvider) {
	RestangularProvider.setBaseUrl('http://localhost:8080/api');
});
