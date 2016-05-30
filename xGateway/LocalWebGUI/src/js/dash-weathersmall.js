'use strict';

// create the weather module
var dashWeathersmall = angular.module('dashWeathersmall', []);

dashWeathersmall
		.factory(
				'weatherService',
				[
						'$http',
						'$q',
						function($http, $q) {
							function getWeather(city, country) {
								var deferred = $q.defer();
								$http
										.get(
												"https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22"
														+ city
														+ "%2C%20"
														+ country
														+ "%22)%20and%20u%3D'"
														+ "c"
														+ "'&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys")
										.success(
												function(data) {
													deferred
															.resolve(data.query.results.channel);
													// console.log
													// (data.query.results.channel.item.condition.code)
													console.log(data)
												})
										.error(
												function(err) {
													console
															.log('Error retrieving weather');
													deferred.reject(err);
												});
								return deferred.promise;
							}

							return {
								getWeather : getWeather
							};
						} ]);

// two small components
// *********************************************
dashWeathersmall.component('dashWeathersmalltoday', {
	// the component template
	templateUrl : 'templates/dash-weathersmalltoday.html',
	bindings : {
		content : '=',
		label_text : '='
	},
	controller : dashWeatherController
});

dashWeathersmall.component('dashWeathersmallforecast', {
	// the component template
	templateUrl : 'templates/dash-weathersmallforecast.html',
	bindings : {
		content : '<'
	},
	controller : dashWeatherController
});

// Controller definition
// **********************************************************************
function dashWeatherController() {
}

dashWeathersmall
		.controller(
				'dashWeatherController',
				[
						'$scope',
						'weatherService',
						function($scope, weatherService) {
							function fetchWeather(city, country) {
								weatherService
										.getWeather(city, country)
										.then(
												function(data) {
													$scope.place = data;

													if ((data.item.forecast[1].text)
															.indexOf("Sunny") >= 0) {
														$scope.greencom_forcast_text = "Good PV production expected for tomorrow: if you can wait, hold your energy consumption!"
													} else {
														$scope.greencom_forcast_text = "Low PV production expected for tomorrow."
													}
													// console.log('data is ' +
													// $scope.place.condition.text);//this
													// is sunny
												});
							}

							$scope.findWeather = function(city, country) {
								$scope.place = '';
								fetchWeather(city, country);
							};
						} ]);
