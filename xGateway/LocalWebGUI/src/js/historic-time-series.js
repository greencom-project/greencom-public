/*
 * Dash.js - Historic Time Series Widget
 * 
 * Copyright (c) 2016 Dario Bonino - Istituto Superiore Mario Boella
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
'use strict';

// declare the angula module
var dashHistoricTimeSeries = angular.module('dashHistoricTimeSeries', [ 'nvd3',
		'ui.bootstrap' ]);

// controller for sample data
dashHistoricTimeSeries.controller('SampleCtrl', function($scope) {
});

// define the historic time series component
// TODO: export Y scale range settings
dashHistoricTimeSeries.component('dashHistoricTimeSeries', {
	// the component template
	templateUrl : 'templates/dash-historic-time-series.html',
	// bound variables (i.e., data and configurations received from "outside"
	bindings : {
		// the x axis label
		dashXLabel : '<',
		// the y axis label
		dashYLabel : '<',
		// the graph height
		dashHeight : '<',
		// the graph data
		dashData : '<',
		// the widget label
		dashLabel : '<',
		// the interpolation mode
		dashInterpolate : '<',
		// the min Y
		dashMinY : '<',
		// the max Y
		dashMaxY : '<',
		// the output callback
		onUpdate : '&',
		// sensor name
		dashSensorName : '<',
	},
	// the component controller where the component handling logic is defined
	controller : dashHistoricTimeSeriesController
});

// the component controller
function dashHistoricTimeSeriesController($scope, $element, $attrs) {
	// debug only
	// console.log(this.dashOptions);

	// store this controller in an easier to use variable to avoid scope
	// confusion
	var ctrl = this;
	// as a default value
	var sensor = 'sensor_meter_mains';
	// --- default values for the start and end date fields
	// TODO: check if these initial settings can be removed

	// end fixed at today
	ctrl.endDate = new Date();
	// start one year ago
	ctrl.startDate = new Date(new Date().setDate(new Date().getDate() - 1));
	/**
	 * Sets the chart options by mixing fixed settings with settings inherited
	 * through bindings
	 */
	ctrl.dashOptions = {
		chart : {
			type : 'lineChart',
			// chart height: this shall be customizable...
			height : (ctrl.dashHeight != undefined) ? ctrl.dashHeight : 550,
			// the margins around the chart area
			margin : {
				top : 20,
				right : 20,
				bottom : 120,
				left : 55
			},
			// functions to get x and y from data
			x : function(d) {
				if (d != undefined)
					return d.x;
			},
			y : function(d) {
				if (d != undefined)
					return d.y;
			},
			// x-axis settings
			xAxis : {
				// set the label
				axisLabel : ctrl.dashXLabel,
				// set ticks
				tickFormat : function(d) {
					return d3.time.format("%Y-%m-%dT%H:%M:%S")(new Date(d))
				},
				// set type of scale
				scale : d3.time.scale(),
				// rotate the dates
				rotateLabels : -45
			},
			yAxis : {
				// set the label
				axisLabel : ctrl.dashYLabel,
				// set ticks
				tickFormat : function(d) {
					return d3.format('.02f')(d);
				},
				axisLabelDistance : -10
			},
			// enable zoom functions
			zoom : {
				enabled : true,
				// scaleExtent : [ 1, 10 ],
				useFixedDomain : false,
				useNiceScale : true,
				horizontalOff : false,
				verticalOff : true,
				// set the event for zoom reset
				unzoomEventType : "dblclick.zoom"
			},
			interpolate : (ctrl.dashInterpolate != undefined) ? ctrl.dashInterpolate
					: 'basis',
			forceY : [ ctrl.dashMinY, ctrl.dashMaxY ]
		// call back for chart changes, not needed
		/*
		 * dispatch : { stateChange : function(e) { console.log('stateChange') }
		 */
		}
	}

	/**
	 * Date pickers configurations and business logic
	 */

	/**
	 * Simple function to find data series extremes over X TODO: check if it can
	 * be done better, e.g., by using d3 functions...
	 * 
	 * MUST be defines before using it, do not move this function declaration
	 * 
	 * @param data
	 *            The data from which extracting the extremes
	 * @param min
	 *            A boolean value that if true triggers minimum search,
	 *            otherwise a maximum search
	 */
	ctrl.findMinMaxDate = function(data, min) {

		// initialize at the current date
		var minDate = new Date();

		// iterate over series in data
		for ( var dataSeries in data) {
			// iterate over values in a single series
			for ( var value in data[dataSeries].values) {
				// search for min/max
				if (min) {
					if (data[dataSeries].values[value].x < minDate)
						minDate = new Date(data[dataSeries].values[value].x);
				} else {
					if (data[dataSeries].values[value].x > minDate)
						minDate = new Date(data[dataSeries].values[value].x);
				}
			}
		}

		// return the found extreme (min/max)
		return minDate;
	};

	/**
	 * Date picker options, shared between all the widget date pickers
	 */
	ctrl.dateOptions = {
		dateDisabled : false,
		formatYear : 'yy',
		maxDate : ctrl.findMinMaxDate(ctrl.dashData, false),
		minDate : new Date("01-01-1970"),
		startingDay : 1,
		popupPlacement : 'left-top'
	};

	/**
	 * Event handling and pop-up management for the startDate date picker
	 */

	// status flag
	ctrl.isPickerOpen = false;

	// event handling
	ctrl.pickerOpen = function($event) {
		// block event propagation to keep the popup open
		$event.preventDefault();
		$event.stopPropagation();
		// set the status at open
		ctrl.isPickerOpen = true;
	};

	/**
	 * Event handling and pop-up management for the endDate date picker
	 */

	// status flag
	ctrl.isPickerOpen2 = false;

	// event handling
	ctrl.pickerOpen2 = function($event) {
		// block event propagation to keep the popup open
		$event.preventDefault();
		$event.stopPropagation();
		// set the status at open
		ctrl.isPickerOpen2 = true;
	};

	/**
	 * Component output, signals changes in the visualized data range through
	 * calls to the on-update callback
	 */
	ctrl.updateRange = function(sensor_name) {
		// check that required data is available
		if (sensor_name != undefined) {
			sensor = sensor_name;
		}
		if ((ctrl.startDate != undefined) && (ctrl.endDate != undefined)) {
			// force update of the visible x range to host the selected period
			// [startDate, endDate]
			console.log(sensor)
			ctrl.dashOptions.chart.forceX = [ sensor, ctrl.startDate,
					ctrl.endDate ];

			// call the callback passing the updated x range
			ctrl.onUpdate({
				sensor : sensor,
				startDate : ctrl.startDate,
				endDate : ctrl.endDate
			});

			// debug only
			/*
			 * console.log({ startDate : ctrl.startDate, endDate : ctrl.endDate
			 * });
			 */
		}
	}

	/**
	 * Handler for the "Last week" button
	 */
	ctrl.lastWeek = function() {
		// debug only
		// console.log("Last week!!");

		// end date is now
		ctrl.endDate = new Date();

		// start date is 7 days ago
		ctrl.startDate = new Date(new Date().setDate(new Date().getDate() - 7));

		// trigger range update handler
		ctrl.updateRange();
	}

	/**
	 * Handler for the "Last month" button
	 */
	ctrl.lastDay = function() {
		// debug only
		// console.log("Last month!!");

		// end date is now
		ctrl.endDate = new Date();

		// start date is one day ago
		ctrl.startDate = new Date(new Date().setDate(new Date().getDate() - 1));
		// trigger range update handler
		ctrl.updateRange();
	}

	// /**
	// * Handler for the "Last year" button
	// */
	// ctrl.lastYear = function() {
	// // debug only
	// // console.log("Last year!!");

	// // start date is now
	// ctrl.endDate = new Date();

	// // end date is one year ago
	// ctrl.startDate = new Date(new Date()
	// .setFullYear(new Date().getFullYear() - 1));
	// // trigger range update handler
	// ctrl.updateRange();
	// }

	/**
	 * Handle data series change
	 */
	ctrl.$onChanges = function(changes) {
		if (changes.dashData.currentValue != undefined) {
			// update start and stop date for date pickers
			ctrl.startDate = ctrl.findMinMaxDate(ctrl.dashData, true);
			ctrl.endDate = ctrl.findMinMaxDate(ctrl.dashData, false);
			// reset the x axis range
			// TODO: check if needed
			ctrl.dashOptions.chart.forceX = [ ctrl.startDate, ctrl.endDate ];
		}
	}
};
