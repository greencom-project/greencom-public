package eu.greencom.aggregation.service.impl;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import org.junit.Test;

import eu.greencom.aggregation.api.Aggregation;
import eu.greencom.aggregation.service.impl.SensorSumAggregator;
import eu.greencom.api.domain.SampledValue;
import eu.greencom.api.domain.SampledValueImpl;
import static eu.greencom.aggregation.api.Aggregation.*;

public class xAggregatorTest {

	private static final String ID = "id1234";

	private static final List<SampledValue> VALUES = new LinkedList<SampledValue>() {
		{
			add(new SampledValueImpl(ID, ID, 10L, 10.0));
			add(new SampledValueImpl(ID, ID, 20L, 20.0));
			add(new SampledValueImpl(ID, ID, 30L, 30.0));
			add(new SampledValueImpl(ID, ID, 40L, 40.0));
			add(new SampledValueImpl(ID, ID, 50L, 50.0));
		}
	};

	/**
	 * Tests retrieval of a value closest to given timestamp
	 */
	@Test
	public void testClosestValue() {
		SensorSumAggregator d = new SensorSumAggregator();
		assertEquals(10.0, d.getClosestValue(-10L, VALUES), 0);
		assertEquals(20.0, d.getClosestValue(24L, VALUES), 0);
		assertEquals(30.0, d.getClosestValue(30L, VALUES), 0);
		assertEquals(40.0, d.getClosestValue(35L, VALUES), 0);
		assertEquals(50.0, d.getClosestValue(100L, VALUES), 0);
	}

}
