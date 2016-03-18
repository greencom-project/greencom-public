// Same as implementation classes - access to protected and package methods
package eu.greencom.mgm.gridtopology.service.impl;

import static eu.greencom.mgm.gridtopology.service.GridTopologyService.TYPE_CONDUCTOR;
import static eu.greencom.mgm.gridtopology.service.GridTopologyService.TYPE_ENERGY_CONSUMER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.greencom.mgm.gridtopology.service.GridTopologyService;

public class TestGridTopologyService {

	GridTopologyService service;

	@Before
	public void before() {
		service = new GridTopologyServiceImpl();	
		Map properties = new HashMap();
		properties.put(GridTopologyServiceImpl.PROPERTY_TOPOLOGY_FILE_LOCAL,"/furPowerLines.js");
		((GridTopologyServiceImpl) service).activate(properties);
	}

	@After
	public void after() {
		((GridTopologyServiceImpl) service).deactivate();
	}

	@Test
	public void testMicrogrids() {
		assertEquals(38, service.getMicrogrids().size());
	}

	@Test
	public void testEnergyConsumers() {
		List<String> l = service.getEnergyConsumers("21383");
		assertEquals(10, l.size());
		// 21383_T1U1
		assertTrue(l.contains("house_house8"));
		// 21383_T1U3
		assertTrue(l.contains("house_house9"));
		assertTrue(l.contains("house_house10"));
		assertTrue(l.contains("house_house13"));
		assertTrue(l.contains("house_house14"));
		assertTrue(l.contains("house_house16"));
		assertTrue(l.contains("house_house17"));
		assertTrue(l.contains("house_house18"));
		assertTrue(l.contains("house_house19"));
		// 21383_T1U4
		assertTrue(l.contains("house_house11"));		
	}

	@Test
	public void testConsumerResources() {
		// same as above
		List<String> l = service.getResourcesByType("21383",
				TYPE_ENERGY_CONSUMER);
		assertEquals(10, l.size());
	}

	@Test
	public void testRadials() {
		List<String> l = service.getResourcesByType("21383", TYPE_CONDUCTOR);
		assertEquals(4, l.size());
		assertTrue(l.contains("21383_T1U1"));
		assertTrue(l.contains("21383_T1U2"));
		assertTrue(l.contains("21383_T1U3"));
		assertTrue(l.contains("21383_T1U4"));
	}

	@Test
	public void testSelf() {
		// Request matches only the radial itself
		List<String> l = service.getResourcesByType("21383_T1U1",
				TYPE_CONDUCTOR);
		assertEquals(1, l.size());
		assertTrue(l.contains("21383_T1U1"));
	}

}