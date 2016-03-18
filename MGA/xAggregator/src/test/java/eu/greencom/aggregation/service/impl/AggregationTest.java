package eu.greencom.aggregation.service.impl;

import static eu.greencom.aggregation.api.Aggregation.DEFAULT_TIME_ZONE;
import static eu.greencom.aggregation.api.Aggregation.PROPERTY_AGGREGATION_PERIOD;
import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import eu.greencom.aggregation.api.Aggregation;

public class AggregationTest {

	@Test
	public void testAggregationTimeHours() {
		Calendar reference = Calendar.getInstance(DEFAULT_TIME_ZONE);
		// HH:12:00:00
		reference.set(Calendar.HOUR_OF_DAY, 17);
		reference.set(Calendar.MINUTE, 12);
		reference.set(Calendar.SECOND, 0);
		reference.set(Calendar.MILLISECOND, 0);
		Aggregation a = new Aggregation();
		// Every 5 minutes
		a.put(PROPERTY_AGGREGATION_PERIOD, "PT3H5M");

		// normalize "17:12:00" to "16:10:00"
		long now = reference.getTimeInMillis();
		reference.set(Calendar.HOUR_OF_DAY, 15);
		reference.set(Calendar.MINUTE, 10);
		System.out.println(new Date(a.getAggregationTime(
				reference.getTimeInMillis(), true)));
		assertEquals(reference.getTimeInMillis(),
				a.getAggregationTime(now, true));

	}

	@Test
	public void testAggregationTimeMinutes() {
		Calendar reference = Calendar.getInstance(DEFAULT_TIME_ZONE);
		// HH:12:00:00
		reference.set(Calendar.MINUTE, 12);
		reference.set(Calendar.SECOND, 0);
		reference.set(Calendar.MILLISECOND, 0);
		Aggregation a = new Aggregation();
		// Every 5 minutes
		a.put(PROPERTY_AGGREGATION_PERIOD, "PT5M");
		// No seconds in reference
		assertEquals(reference.getTimeInMillis(),
				a.getAggregationTime(reference.getTimeInMillis()));
		// HH:14:00:00, no matter when started
		reference.add(Calendar.MINUTE, 2);
		assertEquals(reference.getTimeInMillis(),
				a.getAggregationTime(reference.getTimeInMillis()));

		// HH:14:00:00, normalize minutes to HH:10:00:00
		long now = reference.getTimeInMillis();
		reference.set(Calendar.MINUTE, 10);
		assertEquals(reference.getTimeInMillis(),
				a.getAggregationTime(now, true));
	}

	@Test
	public void testAggregationTimeSeconds() {
		Calendar reference = Calendar.getInstance(DEFAULT_TIME_ZONE);
		// 17:12:33
		reference.set(Calendar.HOUR_OF_DAY, 17);
		reference.set(Calendar.MINUTE, 12);
		reference.set(Calendar.SECOND, 33);
		reference.set(Calendar.MILLISECOND, 0);
		Aggregation a = new Aggregation();
		a.put(PROPERTY_AGGREGATION_PERIOD, "PT3H5M5S");
		long now = reference.getTimeInMillis();
		reference.set(Calendar.HOUR_OF_DAY, 15);
		reference.set(Calendar.MINUTE, 10);
		reference.set(Calendar.SECOND, 30);
		System.out.println(new Date(a.getAggregationTime(
				reference.getTimeInMillis(), true)));
		assertEquals(reference.getTimeInMillis(),
				a.getAggregationTime(now, true));
	}
	
	@Test
	public void testAggregationTimeOnlySeconds() {
		Calendar reference = Calendar.getInstance(DEFAULT_TIME_ZONE);
		reference.set(Calendar.HOUR_OF_DAY, 17);
		reference.set(Calendar.MINUTE, 12);
		reference.set(Calendar.SECOND, 33);
		reference.set(Calendar.MILLISECOND, 0);
		Aggregation a = new Aggregation();
		a.put(PROPERTY_AGGREGATION_PERIOD, "PT3H5M5S");
		long now = reference.getTimeInMillis();
		reference.set(Calendar.HOUR_OF_DAY, 15);
		reference.set(Calendar.MINUTE, 10);
		reference.set(Calendar.SECOND, 30);
		System.out.println(new Date(a.getAggregationTime(
				reference.getTimeInMillis(), true)));
		assertEquals(reference.getTimeInMillis(),
				a.getAggregationTime(now, true));
	}


	@Test
	public void testTimeInterval() {
		Aggregation a = new Aggregation();
		a.put(PROPERTY_AGGREGATION_PERIOD, "PT30S");
		assertEquals(1000 * 30, a.getAggregationInterval());

		a.put(PROPERTY_AGGREGATION_PERIOD, "PT1H30M");
		assertEquals(1000 * 60 * 90, a.getAggregationInterval());
	}

}
