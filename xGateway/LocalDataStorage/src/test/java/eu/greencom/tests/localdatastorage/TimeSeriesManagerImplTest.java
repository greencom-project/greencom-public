package eu.greencom.tests.localdatastorage;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mongodb.util.JSON;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import eu.greencom.api.domain.SampledValue;
import eu.greencom.localdatastorage.impl.MongoLocalDataStorage;
import eu.greencom.localdatastorage.timeseries.impl.LocalTimeSeriesManager;
import eu.greencom.xgateway.localdatastorage.api.service.LocalDataStorage;

public class TimeSeriesManagerImplTest {

	private static LocalTimeSeriesManager timeSeriesManager;

	@BeforeClass
	public static void setUp() {
		long ts = 1234567891;
		LocalDataStorage l = mock(LocalDataStorage.class);
		Map<String, Object> query = new HashMap<String, Object>();
		query.put("sent", false);
		List<Map<String, Object>> resl = new LinkedList<Map<String, Object>>();
		Map<String, Object> res = new HashMap<String, Object>();
		res.put("_id", "1");
		res.put("timeSeriesID", "2");
		res.put("deviceID", "3");
		res.put("timestamp", ts);
		res.put("value", 123.2);
		res.put("sent", true);
		resl.add(res);
		when(
				l.list(LocalTimeSeriesManager.SAMPLEDVALUECOLLECTIONNAME,
						query, LocalDataStorage.NO_LIMIT)).thenReturn(resl);

		timeSeriesManager = new LocalTimeSeriesManager();
		timeSeriesManager.setStore(l);
	}

	@Test
	public void testGetUnsentValues() {
		List<SampledValue> s = timeSeriesManager.getUnsentValues(1000);
		assertNotNull(s);
		assertEquals(1, s.size());
		assertEquals("1", s.get(0).get_id());
		assertEquals("2", s.get(0).getTimeSeriesID());
		assertEquals("3", s.get(0).getDeviceID());
		assertEquals(123.2, s.get(0).getValue(), 0.1);
		assertEquals(1234567891, s.get(0).getTimestamp());
		assertEquals(false, s.get(0).isSent());

	}

}
