// ---------------------------------------------------------------------
// Use this JS to set up and provide interaction for the history charts
// ---------------------------------------------------------------------

// Initialize variables for the charts
var solarEnergyChart;
var solarEnergyChartOptions;
var forecastChart;
var forecastChartOptions;

//Build the chart
function setUpCharts() {
	// Settings for the solar energy chart
	solarEnergyChartOptions = {
		chart: {
			renderTo: 'solarEnergyForecastTableHighchartsId',
			animation: Highcharts.svg,
			backgroundColor: '#FFFFFF',
			borderWidth: 0
		},

		title: {
			text: 'Solar energy production per sqm from yesterday until tomorrow',
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
                    text: 'Solar energy'
                },
                labels: {
                    format: '{value} W/m²',
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
			shared: true
		},
		series: [{
            type: 'spline',
            lineWidth: 2,
            name: 'Solar energy per sqm',
            data: [],
            color: '#0000FF',
            yAxis: 0,		// Assign data to the matching axis
            tooltip: {
            	valueSuffix: ' W'
            },
            marker: {
                enabled: false
            }
        },{
            type: 'spline',
            lineWidth: 2,
            name: 'Solar power production per solar unit',
            data: [],
            color: '#00FF00',
            yAxis: 0,		// Assign data to the matching axis
            tooltip: {
            	valueSuffix: ' W'
            },
            marker: {
                enabled: false
            }
        }]
	};

	// Settings for the mockup forecast chart
	forecastChartOptions = {
		chart: {
            renderTo: 'forecastChartId',
			animation: Highcharts.svg,
			backgroundColor: '#FFFFFF',
			borderWidth: 0
        },
        title: {
            text: 'Energy consumption forecast for a GreenCom grid element'
        },
        subtitle: {
            text: 'Source: GreenCom Forecasting Services'
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
        yAxis: [{  // First y-Axis
                title: {
                    text: 'Total load in %'
                }
        	}, { // Secondary y-Axis
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
//                min: 0,  		// Set the min and max value for this axis
//                max: 50,		// Set the min and max value for this axis
                opposite: true
            }],
        tooltip: {
            shared: true,
            valueSuffix: ' kWh'
        },
        credits: {
			enabled: false
		},
        plotOptions: {
            area: {
                stacking: 'normal',
                lineColor: '#666666',
                lineWidth: 1,
                marker: {
                    enabled: false
                }
            },
            line: {
            	marker: {
                    enabled: false
                },
                shadow: false,
                lineWidth: 2
            }
        },
        series: [
          {
        	type: 'area',
            name: 'Energy consumption',
            yAxis: 0,		// Assign data to the matching axis
            color: '#99FFFF'
          },{
        	type: 'line',
            step: 'right',
            name: 'Energy price per MWh',
            color: '#9900DD',
            yAxis: 1,		// Assign data to the matching axis
            tooltip: {
            	valueSuffix: ' EUR/MWh'
            }
          }
        ]
	};
	
	// Initialize charts with the given options
	solarEnergyChart = new Highcharts.Chart(solarEnergyChartOptions);
	forecastChart = new Highcharts.Chart(forecastChartOptions);
	
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
