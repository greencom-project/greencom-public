// ---------------------------------------------------------------------
// Use this JS to set up and provide interaction for the history charts
// ---------------------------------------------------------------------

// Initialize variables for the charts
var energyDataChart;
var energyDataChartOptions;

// Two Options: Percent-Axis or Watts-Axis
var energyDataChartPercentAxis;
var energyDataChartWattsAxis;

var weatherDataChart;
var weatherDataChartOptions;
var loadDataChart;
var loadDataChartOptions;
var aggregatedDataChart;
var aggregatedDataChartOptions;

//Build the chart
function setUpCharts() {
	// Settings for the energy chart
	energyDataChartOptions = {
		chart: {
			renderTo: 'furDetailsDataTableHighcharts',
			zoomType: 'x',
			animation: Highcharts.svg,
			backgroundColor: '#FFFFFF',
			borderWidth: 0,
//			events: {
//                selection: function(event) {
//                	// Event wird vor dem neurendern des Charts geworfen
//                    // if (event.xAxis) {
//                    // 	furDataSupplier.setScale('fromEvent');
//                    //     // console.log('min: '+ event.xAxis[0].min +', max: '+ event.xAxis[0].max);
//                    // }
//                    if (event.resetSelection) {
//                    	// Wait a bit before resetting the scale
//                    	setTimeout(function() {	
//							setScale(null, 1, 'fromEvent');  // TBD: Maybe does not work, need to check
//						}, 10);
//                    }
//                }
//            }
		},

		title: {
			text: 'Transformer station load and energy prices over time',
            x: -40 //center
		},
		subtitle :{
			text: 'Source: GreenCom DataWarehouse',
            x: -40
		},
		xAxis: {
			type: 'datetime',
			dateTimeLabelFormats: {
				millisecond: '%H:%M:%S.%L',
				second: '%H:%M:%S',
				minute: '%H:%M',
				hour: '%H:%M',
				day: '%e. %b',
				week: '%e. %b',
				month: '%b \'%y',
				year: '%Y'
			},
			minRange: 6*60*60*1000, 		// Don't allow the graph to show less than six hours	
        	// tickInterval: 12*60*60*1000,	// = 12 hours
			gridLineWidth: 1
		},
		yAxis: [
			{ // Secondary y-Axis
				id: 'price_axis',
                title: {
                    text: 'Energy price per MWh',
                    style: {
                        color: '#4572A7'
                    }
                },
                labels: {
                    format: '{value} EUR',
                    style: {
                        color: '#4572A7'
                    }
                },
                opposite: true
            }
		],
		legend:{
			enabled: true,
			layout: 'horizontal',
            align: 'center',
            verticalAlign: 'bottom',
            floating: false
            // borderWidth: 0
		},
		exporting: {
			enabled: false
		},
		credits: {
			enabled: false
		},
		plotOptions: {
			area: {
                lineWidth: 2,
                marker: {
                    enabled: false
                },
                shadow: false,
                states: {
                    hover: {
                        lineWidth: 1
                    }
                },
                threshold: 0 // Lower border set to 0; Disable by setting 'null'
            },
            line: {
            	marker: {
                    enabled: false
                },
                shadow: false
            },
            column: {
                pointPadding: 0.2,
                borderWidth: 0
            }
		},
		tooltip : {
			shared: true,
		},
		series: []
	};
	
	energyDataChartWattsAxis = {
		id: 'watt_axis',
		title: {
            text: 'Load'
        },
        labels: {
            format: '{value} W',
            style: {
                color: '#89A54E'
            }
        },
	};
	
	energyDataChartPercentAxis = {
			id: 'percent_axis',
			title: {
	            text: 'Percent'
	        },
	        labels: {
	            format: '{value} %',
	            style: {
	                color: '#89A54E'
	            }
	        },
	            min: 0,  		// Set the min and max value for this axis
//	            max: 100		// Set the min and max value for this axis
		};
	
	// Settings for the weather chart
	weatherDataChartOptions = {
		chart: {
			renderTo: 'weatherDetailsDataTableHighcharts',
			zoomType: 'x',
			animation: Highcharts.svg,
			backgroundColor: '#FFFFFF',
			borderWidth: 0
		},
		title: {
			text: 'Weather conditions over time',
            x: -40 // center
		},
		subtitle :{
			text: 'Source: GreenCom DataWarehouse / Weather',
            x: -40
		},
		xAxis: {
			type: 'datetime',
			dateTimeLabelFormats: {
				millisecond: '%H:%M:%S.%L',
				second: '%H:%M:%S',
				minute: '%H:%M',
				hour: '%H:%M',
				day: '%e. %b',
				week: '%e. %b',
				month: '%b \'%y',
				year: '%Y'
			},
			minRange: 6*60*60*1000, 		// Don't allow the graph to show less than six hours
			// tickInterval: 12*60*60*1000,	// = 12 hours
			gridLineWidth: 1
		},
		yAxis: [
			{ // First y-Axis
				title: {
                    text: 'Temperature in °C'
                },
                labels: {
                    format: '{value}°C',
                    style: {
                        color: '#89A54E'
                    }
                }
			}, { // Secondary yAxis
                title: {
                    text: 'Windspeed in km/h',
                    style: {
                        color: '#4572A7'
                    }
                },
                labels: {
                    format: '{value} km/h',
                    style: {
                        color: '#4572A7'
                    }
                },
                // min: 0,  		// Set the min and max value for this axis
                // max: 100,		// Set the min and max value for this axis
                opposite: true
            }
		],
		legend: {
			enabled: true,
			layout: 'horizontal',
            align: 'center',
            verticalAlign: 'bottom',
            floating: false
            // borderWidth: 0
		},
		exporting: {
			enabled: false
		},
		credits: {
			enabled: false
		},
		plotOptions: {
			area: {
                lineWidth: 2,
                marker: {
                    enabled: false
                },
                shadow: false,
                states: {
                    hover: {
                        lineWidth: 1
                    }
                },
                threshold: 0 // Lower border set to 0; Disable by setting 'null'
            },
            line: {
            	marker: {
                    enabled: false
                },
                shadow: false
            },
		},
		tooltip : {
			shared: true,
		},
		series: [{
            type: 'spline',
            lineWidth: 2,
            name: 'Temperature',
            data: [],
            color: '#000000',
            yAxis: 0,		// Assign data to the matching axis
            tooltip: {
            	valueSuffix: '°C'
            },
            marker: {
                enabled: false
            }
        },{
            type: 'spline',
            lineWidth: 1,
            name: 'Windspeed in km/h',
            data:  [],
            color: '#0000FF',
            yAxis: 1,		// Assign data to the matching axis
            tooltip: {
            	valueSuffix: ' km/h'
            },
            marker: {
                enabled: false
            }
        },{
            type: 'spline',
            lineWidth: 1,
            name: 'Humidity',
            data:  [],
            color: '#FF00AA',
            yAxis: 1,		// Assign data to the matching axis
            tooltip: {
            	valueSuffix: ' %'
            },
            marker: {
                enabled: false
            }
        }]
	};
	
	aggregatedDataChartOptions = {
		chart: {
			renderTo: 'mgaDataChartId',
			zoomType: 'x',
			animation: Highcharts.svg,
			backgroundColor: '#FFFFFF',
			borderWidth: 0
		},
		title: {
			text: 'Load value over time for all houses within the selected microgrid',
            x: -40 // center
		},
		subtitle :{
			text: 'Source: GreenCom DataWarehouse / MGA data',
            x: -40
		},
		xAxis: {
			type: 'datetime',
			dateTimeLabelFormats: {
				millisecond: '%H:%M:%S.%L',
				second: '%H:%M:%S',
				minute: '%H:%M',
				hour: '%H:%M',
				day: '%e. %b',
				week: '%e. %b',
				month: '%b \'%y',
				year: '%Y'
			},
			minRange: 6*60*60*1000, 		// Don't allow the graph to show less than six hours
			// tickInterval: 12*60*60*1000,	// = 12 hours
			gridLineWidth: 1
		},
		yAxis: [
			{ // First y-Axis
				title: {
                    text: 'Consumption in Watt hours'
                },
                labels: {
                    format: '{value} Wh',
                    style: {
                        color: '#89A54E'
                    }
                }
			}
		],
		legend: {
			enabled: true,
			layout: 'horizontal',
            align: 'center',
            verticalAlign: 'bottom',
            floating: false
            // borderWidth: 0
		},
		exporting: {
			enabled: false
		},
		credits: {
			enabled: false
		},
		plotOptions: {
			area: {
                lineWidth: 2,
                marker: {
                    enabled: false
                },
                shadow: false,
                states: {
                    hover: {
                        lineWidth: 1
                    }
                },
                threshold: 0 // Lower border set to 0; Disable by setting 'null'
            },
            line: {
            	marker: {
                    enabled: false
                },
                shadow: false
            },
		},
		tooltip : {
			shared: true,
		},
		series: [{
            type: 'spline',
            lineWidth: 2,
            name: 'Consumption',
            data: [],
            color: '#000000',
            yAxis: 0,		// Assign data to the matching axis
            tooltip: {
            	valueSuffix: ' Wh'
            },
            marker: {
                enabled: false
            }
        }]
	};
	
	loadDataChartOptions = {
		chart: {
			renderTo: 'smartMeterDetailsHighcharts',
			zoomType: 'x',
			animation: Highcharts.svg,
			backgroundColor: '#FFFFFF',
			borderWidth: 0
		},
		title: {
			text: 'Load value over time',
            x: -40 // center
		},
		subtitle :{
			text: 'Source: GreenCom DataWarehouse / Load values',
            x: -40
		},
		xAxis: {
			type: 'datetime',
			dateTimeLabelFormats: {
				millisecond: '%H:%M:%S.%L',
				second: '%H:%M:%S',
				minute: '%H:%M',
				hour: '%H:%M',
				day: '%e. %b',
				week: '%e. %b',
				month: '%b \'%y',
				year: '%Y'
			},
			minRange: 6*60*60*1000, 		// Don't allow the graph to show less than six hours
			// tickInterval: 12*60*60*1000,	// = 12 hours
			gridLineWidth: 1
		},
		yAxis: [
			{ // First y-Axis
				title: {
                    text: 'Consumption in Watts'
                },
                labels: {
                    format: '{value} W',
                    style: {
                        color: '#89A54E'
                    }
                }
			}
		],
		legend: {
			enabled: true,
			layout: 'horizontal',
            align: 'center',
            verticalAlign: 'bottom',
            floating: false
            // borderWidth: 0
		},
		exporting: {
			enabled: false
		},
		credits: {
			enabled: false
		},
		plotOptions: {
			area: {
                lineWidth: 2,
                marker: {
                    enabled: false
                },
                shadow: false,
                states: {
                    hover: {
                        lineWidth: 1
                    }
                },
                threshold: 0 // Lower border set to 0; Disable by setting 'null'
            },
            line: {
            	marker: {
                    enabled: false
                },
                shadow: false
            },
		},
		tooltip : {
			shared: true,
		},
		series: [{
            type: 'spline',
            lineWidth: 2,
            name: 'Consumption',
            data: [],
            color: '#000000',
            yAxis: 0,		// Assign data to the matching axis
            tooltip: {
            	valueSuffix: ' W'
            },
            marker: {
                enabled: false
            }
        }]
	};
	
	// Initialize charts with the given options
	energyDataChart = new Highcharts.Chart(energyDataChartOptions);
	weatherDataChart = new Highcharts.Chart(weatherDataChartOptions);
//	loadDataChart = new Highcharts.Chart(loadDataChartOptions);
	
	Highcharts.setOptions({
		global: {
			useUTC: false
		}
	});
}

function initCharts() {
	setUpCharts();
}

// Set up the charts
initCharts();

