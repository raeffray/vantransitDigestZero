package com.raeffray.graph.handler;

import org.junit.Before;
import org.junit.Test;

public class HandlersTest {

	RouteNodeHandler handler;
	CommonNodesHandler commonsHandler;

	@Before
	public void prepare() {
		handler = new RouteNodeHandler();
		commonsHandler = new CommonNodesHandler();
	}

	//@Test
	public void createSetRouteNodeTest() {
		handler.createSetRouteNode();

	}
	
	@Test
	public void createAgencyTest() {
		commonsHandler.createStopTimesNode();

	}

}
