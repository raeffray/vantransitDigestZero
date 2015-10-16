package com.raeffray.rest.cient;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.text.MessageFormat;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.raeffray.graph.RelationshipDescriber;
import com.raeffray.json.JsonUtils;
import com.raeffray.raw.data.Agency;
import com.raeffray.raw.data.StopTimes;
import com.raeffray.rest.cient.enums.GraphResourceCatalog;

public class RestClietTest {

	//@Test
	public void nodeFetchTest() throws Exception {
		JSONObject fetchNodeById = RestClient.getInstance().fetchNodeById(41);
		fetchNodeById.get("data");
	}

	//@Test
	public void createBatchRequestTest() throws Exception {
		BatchOperationRequest request = new BatchOperationRequest();
		request.addOperation(0, GraphResourceCatalog.BATCH_OPERATION_NODE_FETCH
				.getHttpMethod(), MessageFormat.format(
				GraphResourceCatalog.BATCH_OPERATION_NODE_FETCH.getResource(),
				40), "[\"FOO\"]");

		assertThat(
				request.parseJson(),
				is(equalTo("[{\"id\":0,\"method\":\"GET\",\"to\":\"/node/40\",\"body\":[\"FOO\"]}]")));
	}

	//@Test
	public void createBatchRequestWithNullBodyTest() throws Exception {
		BatchOperationRequest request = new BatchOperationRequest();
		request.addOperation(0, GraphResourceCatalog.BATCH_OPERATION_NODE_FETCH
				.getHttpMethod(), MessageFormat.format(
				GraphResourceCatalog.BATCH_OPERATION_NODE_FETCH.getResource(),
				40), null);
		assertThat(
				request.parseJson(),
				is(equalTo("[{\"id\":0,\"method\":\"GET\",\"to\":\"/node/40\"}]")));
	}
	
	//@Test
	public void testParseRelationDecriber() throws Exception{
		RelationshipDescriber relDesc = new RelationshipDescriber("TEST");
		relDesc.addAttribute("A",1);
		relDesc.addAttribute("B",2);
		relDesc.addAttribute("C",3);
		System.out.println(relDesc.parseJson());
	}
	
	@Test
	public void testParseStopTimes() throws Exception{
		StopTimes stopTimes = new StopTimes();
		
		stopTimes.setStop_sequence("1");
		
		System.out.println(JsonUtils.parseJson(stopTimes));
	
	}

	//@Test
	public void batchFetchNodeTest() throws Exception {
		JSONArray node = RestClient.getInstance().fetchNodes(40);
		Object object = node.get(0);
		String value = ((JSONObject) ((JSONObject) object).get("body")).get(
				"data").toString();
		assertThat(value,
				is(equalTo("{\"name\":\"Renato\",\"surname\":\"Barbosa\"}")));
	}
	
	//@Test
	public void batchFetchMultipleNodeTest() throws Exception {
		JSONArray node = RestClient.getInstance().fetchNodes(40,41);
		Object object = node.get(0);
		String value = ((JSONObject) ((JSONObject) object).get("body")).get(
				"data").toString();
		assertThat(value,
				is(equalTo("{\"name\":\"Renato\",\"surname\":\"Barbosa\"}")));
		object = node.get(1);
		value = ((JSONObject) ((JSONObject) object).get("body")).get(
				"data").toString();
		assertThat(value,
				is(equalTo("{\"name\":\"Traci\",\"surname\":\"Miles\"}")));
	
	}
	
	//@Test
	public void batchCreateNodeTest() throws Exception {
		
		String[] labels = {"FOOX","TEST"};
		
		Agency agency = new Agency();
		agency.setAgency_name("AG_DULL");
		agency.setAgency_url("http://that.one");
		
		Long nodeId = RestClient.getInstance().createNode(labels, agency);
		
		int a = 1;
		
	}
	

}
