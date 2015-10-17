package com.raeffray.graph.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Test;

import com.raeffray.csv.CSVReader;
import com.raeffray.graph.RelationshipDescriber;
import com.raeffray.raw.data.Agency;
import com.raeffray.raw.data.Calendar;
import com.raeffray.raw.data.CalendarDates;
import com.raeffray.raw.data.RawData;
import com.raeffray.raw.data.Routes;
import com.raeffray.raw.data.Shapes;
import com.raeffray.raw.data.StopTimes;
import com.raeffray.raw.data.Stops;
import com.raeffray.raw.data.Transfers;
import com.raeffray.raw.data.Trips;
import com.raeffray.reflection.ReflectionData;
import com.raeffray.rest.cient.RestClient;

public class RouteNodeHandler {

	static Logger logger = Logger.getLogger(RestClient.class);

	private List<Agency> agencyList;

	private List<Transfers> TransferList;

	private List<Routes> routeList;

	private List<Trips> tripList;

	private List<Shapes> shapeList;

	private List<Stops> stopList;

	private List<Calendar> serviceList;

	private List<CalendarDates> calendarDateList;

	private List<StopTimes> stopTimesList;

	Map<String, Long> serviceNodesIds;

	Map<String, Long> agencyNodesIds;

	Map<String, Long> serviceDatesNodesIds;

	public void loadCSVs() throws Exception {

		CSVReader reader = new CSVReader();

		logger.info("Loading [Agency]");
		agencyList = ReflectionData.getInstance().buildList(Agency.class,
				reader.readCSVForData(Agency.class));

		logger.info("Loading [Routes]");
		routeList = ReflectionData.getInstance().buildList(Routes.class,
				reader.readCSVForData(Routes.class));

		logger.info("Loading [Trips]");
		tripList = ReflectionData.getInstance().buildList(Trips.class,
				reader.readCSVForData(Trips.class));

		logger.info("Loading [Stops]");
		stopList = ReflectionData.getInstance().buildList(Stops.class,
				reader.readCSVForData(Stops.class));

		logger.info("Loading [StopTimes]");
		stopTimesList = ReflectionData.getInstance().buildList(StopTimes.class,
				reader.readCSVForData(StopTimes.class));

		logger.info("Loading [Calendar]");
		serviceList = ReflectionData.getInstance().buildList(Calendar.class,
				reader.readCSVForData(Calendar.class));

		logger.info("Loading [CalendarDates]");
		calendarDateList = ReflectionData.getInstance()
				.buildList(CalendarDates.class,
						reader.readCSVForData(CalendarDates.class));
		
		// List<Transfers> TransferList = ReflectionData.getInstance()
				// .buildList(Transfers.class,
				// reader.readCSVForData(Transfers.class));
		
		// List<Shapes> shapeList = ReflectionData.getInstance().buildList(
				// Shapes.class, reader.readCSVForData(Shapes.class));
	}

	public void start() {
		try {

			loadCSVs();

			createSetServiceNode();

			createAgencyNode();

			for (Agency agency : agencyList) {
				Long agencyNodeId = agencyNodesIds.get(agency.getAgency_id());
				createSetRouteNode(agency.getAgency_id(), agencyNodeId);
			}
			
		} catch (Exception e) {
			logger.error(e);
		}
	}

	private void createAgencyNode() throws Exception {

		logger.info("Creating [Agency] node");
		
		String[] serviceLabels = { "AGENCY", "TEST" };

		List<RawData> rawList = new ArrayList<RawData>();

		rawList.addAll(agencyList);

		JSONArray responseNodes = RestClient.getInstance().createNodes(
				serviceLabels, rawList);

		agencyNodesIds = extractCreatedAgencyNodes(responseNodes);

	}

	private void createSetRouteNode(String agencyId, long agencyNodeId)
			throws Exception {
		
		logger.info("Creating [Route] nodes");

		List<RawData> routesFromAgency = findRouteByAgencyId(routeList,
				agencyId);

		String[] childLabels = { "ROUTE", "TEST" };

		RelationshipDescriber relDesc = new RelationshipDescriber("OPERATES");

		JSONArray responseRouteStructure = RestClient.getInstance()
				.createNodeStructure(routesFromAgency, childLabels, relDesc,
						agencyNodeId);

		Map<String, Long> routeNodesIds = extractCreatedRouteNodes(responseRouteStructure);

		for (RawData rawRoute : routesFromAgency) {
			Routes route = (Routes) rawRoute;
			logger.info("Creating [Route] code ["+route.getRoute_short_name()+"]  node");
			Long routeFather = routeNodesIds.get(route.getRoute_id());
			createSetTripNode(route.getRoute_id(), routeFather);
		}
	}

	private void createSetTripNode(String routeId, long routeNodeId)
			throws Exception {

		logger.info("Creating [Trips] nodes");

		List<RawData> tripsFromRoute = findTripById(tripList, routeId);

		String[] childLabels = { "TRIP", "TEST" };
		RelationshipDescriber relDesc = new RelationshipDescriber("TRAVELS");
		RelationshipDescriber serviceDesc = new RelationshipDescriber("EXECUTE");

		JSONArray responseTripStructure = RestClient.getInstance()
				.createNodeStructureForTrip(tripsFromRoute, childLabels,
						relDesc, serviceDesc, serviceNodesIds, routeNodeId);
		Map<String, Long> tripNodesIds = extractCreatedTripNodes(responseTripStructure);

		for (RawData rawData : tripsFromRoute) {
			Trips trip = (Trips) rawData;
			logger.info("Creating [Trip] tripId ["+trip.getTrip_id()+"]  node");
			Long nodeFather = tripNodesIds.get(trip.getTrip_id());
			createSetStopTimesNode(trip.getTrip_id(), nodeFather);
		}
	}

	private void createSetServiceNode() throws Exception {

		logger.info("Creating [Service] nodes");
		
		String[] serviceLabels = { "SERVICE", "TEST" };

		List<RawData> rawList = new ArrayList<RawData>();

		rawList.addAll(serviceList);

		JSONArray responseNodes = RestClient.getInstance().createNodes(
				serviceLabels, rawList);

		serviceNodesIds = extractCreatedCalendarNodes(responseNodes);

		for (Calendar service : serviceList) {
			Long serviceNodeId = serviceNodesIds.get(service.getService_id());
			createSetServiceDateNode(service.getService_id(), serviceNodeId);
		}
	}

	private void createSetServiceDateNode(String serviceId, long serviceNodeId)
			throws Exception {

		List<RawData> calendarDatesFromTrip = findCalendarDateByServiceId(
				calendarDateList, serviceId);

		String[] childLabels = { "SERVICE_DATE", "TEST" };
		RelationshipDescriber relDesc = new RelationshipDescriber(
				"IN_EFFECT_ON");
		JSONArray responseCalendarDateStructure = RestClient.getInstance()
				.createNodeStructure(calendarDatesFromTrip, childLabels,
						relDesc, serviceNodeId);
		Map<String, Long> tripNodesIds = extractCreatedCalendarNodes(responseCalendarDateStructure);

	}

	private void createSetStopTimesNode(String tripId, long tripNodeId)
			throws Exception {
		
		logger.info("Creating [StopTimes] nodes");

		List<RawData> stopsTimesFromTrip = findStopTimesByTripId(stopTimesList,
				tripId);

		String[] childLabels = { "STOP_TIME", "TEST" };
		RelationshipDescriber relDesc = new RelationshipDescriber("STOPS_AT");

		JSONArray responseCalendarStructure = RestClient.getInstance()
				.createNodeStructure(stopsTimesFromTrip, childLabels, relDesc,
						tripNodeId);
		Map<String, Long> stopTimesNodesIds = extractCreatedStopTimesNodes(responseCalendarStructure);

		for (RawData rawData : stopsTimesFromTrip) {
			StopTimes stopTime = (StopTimes) rawData;
			logger.debug("Creating [StopTimes] for tripId ["+stopTime.getTrip_id()+"]  node");
			Long nodeFather = stopTimesNodesIds.get(stopTime.getStop_id());
			createSetStopsNode(stopTime.getStop_id(), nodeFather);
		}
	}

	private void createSetStopsNode(String stopId, long stopTimeNodeId)
			throws Exception {

		List<RawData> stopsFromTrip = findStopByStopId(stopList, stopId);

		String[] childLabels = { "BUSSTOP", "TEST" };
		RelationshipDescriber relDesc = new RelationshipDescriber("PICKUP_AT");

		JSONArray responseCalendarStructure = RestClient.getInstance()
				.createNodeStructure(stopsFromTrip, childLabels, relDesc,
						stopTimeNodeId);
		Map<String, Long> stopTimesNodesIds = extractCreatedStopTimesNodes(responseCalendarStructure);
	}

	private Map<String, Long> extractCreatedRouteNodes(
			JSONArray responseStructure) throws Exception {
		Map<String, Long> createNodes = new HashMap<String, Long>();
		for (Object object : responseStructure) {

			JSONObject item = (JSONObject) object;

			JSONObject jsBody = (JSONObject) item.get("body");
			if (jsBody != null) {
				JSONObject jsData = (JSONObject) jsBody.get("data");
				JSONObject jsMetadata = (JSONObject) jsBody.get("metadata");
				Object objRouteId = jsData.get("routeId");
				if (objRouteId != null) {
					createNodes.put((String) objRouteId,
							(Long) jsMetadata.get("id"));
				}
			}
		}
		return createNodes;
	}

	private Map<String, Long> extractCreatedTripNodes(
			JSONArray responseStructure) throws Exception {
		Map<String, Long> createNodes = new HashMap<String, Long>();
		for (Object object : responseStructure) {

			JSONObject item = (JSONObject) object;

			JSONObject jsBody = (JSONObject) item.get("body");
			if (jsBody != null) {
				JSONObject jsData = (JSONObject) jsBody.get("data");
				JSONObject jsMetadata = (JSONObject) jsBody.get("metadata");
				Object objRouteId = jsData.get("tripId");
				if (objRouteId != null) {
					createNodes.put((String) objRouteId,
							(Long) jsMetadata.get("id"));
				}
			}
		}
		return createNodes;
	}

	private Map<String, Long> extractCreatedAgencyNodes(
			JSONArray responseStructure) throws Exception {
		Map<String, Long> createNodes = new HashMap<String, Long>();
		for (Object object : responseStructure) {

			JSONObject item = (JSONObject) object;

			JSONObject jsBody = (JSONObject) item.get("body");
			if (jsBody != null) {
				JSONObject jsData = (JSONObject) jsBody.get("data");
				JSONObject jsMetadata = (JSONObject) jsBody.get("metadata");
				Object objRouteId = jsData.get("agencyId");
				if (objRouteId != null) {
					createNodes.put((String) objRouteId,
							(Long) jsMetadata.get("id"));
				}
			}
		}
		return createNodes;
	}

	private Map<String, Long> extractCreatedCalendarNodes(
			JSONArray responseStructure) throws Exception {
		Map<String, Long> createNodes = new HashMap<String, Long>();
		for (Object object : responseStructure) {

			JSONObject item = (JSONObject) object;

			JSONObject jsBody = (JSONObject) item.get("body");
			if (jsBody != null) {
				JSONObject jsData = (JSONObject) jsBody.get("data");
				JSONObject jsMetadata = (JSONObject) jsBody.get("metadata");
				Object objRouteId = jsData.get("serviceId");
				if (objRouteId != null) {
					createNodes.put((String) objRouteId,
							(Long) jsMetadata.get("id"));
				}
			}
		}
		return createNodes;
	}

	private Map<String, Long> extractCreatedStopTimesNodes(
			JSONArray responseStructure) throws Exception {
		Map<String, Long> createNodes = new HashMap<String, Long>();
		for (Object object : responseStructure) {

			JSONObject item = (JSONObject) object;

			JSONObject jsBody = (JSONObject) item.get("body");
			if (jsBody != null) {
				JSONObject jsData = (JSONObject) jsBody.get("data");
				JSONObject jsMetadata = (JSONObject) jsBody.get("metadata");
				Object objRouteId = jsData.get("stopId");
				if (objRouteId != null) {
					createNodes.put((String) objRouteId,
							(Long) jsMetadata.get("id"));
				}
			}
		}
		return createNodes;
	}

	private Map<String, Long> extractCreatedStopNodes(
			JSONArray responseStructure) throws Exception {
		Map<String, Long> createNodes = new HashMap<String, Long>();
		for (Object object : responseStructure) {

			JSONObject item = (JSONObject) object;

			JSONObject jsBody = (JSONObject) item.get("body");
			if (jsBody != null) {
				JSONObject jsData = (JSONObject) jsBody.get("data");
				JSONObject jsMetadata = (JSONObject) jsBody.get("metadata");
				Object objRouteId = jsData.get("stopId");
				if (objRouteId != null) {
					createNodes.put((String) objRouteId,
							(Long) jsMetadata.get("id"));
				}
			}
		}
		return createNodes;
	}

	private List<RawData> findTripById(List<Trips> allTrips, String routeId) {
		List<RawData> foundTrips = new ArrayList<RawData>();
		for (Iterator<Trips> iterator = allTrips.iterator(); iterator.hasNext();) {
			Trips trip = iterator.next();
			if (trip.getRoute_id().equals(routeId)) {
				foundTrips.add(trip);
			}
		}
		return foundTrips;
	}

	private List<RawData> findRouteByAgencyId(List<Routes> allElements,
			String lookupId) {
		List<RawData> found = new ArrayList<RawData>();
		for (Iterator<Routes> iterator = allElements.iterator(); iterator
				.hasNext();) {
			Routes element = iterator.next();
			if (element.getAgency_id().equals(lookupId)) {
				found.add(element);
				iterator.remove();
			}
		}
		return found;
	}

	private List<RawData> findCalendarByServiceId(List<Calendar> allElements,
			String lookupId) {
		List<RawData> found = new ArrayList<RawData>();
		for (Iterator<Calendar> iterator = allElements.iterator(); iterator
				.hasNext();) {
			Calendar element = iterator.next();
			if (element.getService_id().equals(lookupId)) {
				found.add(element);
				iterator.remove();
			}
		}
		return found;
	}

	private List<RawData> findCalendarDateByServiceId(
			List<CalendarDates> allElements, String lookupId) {
		List<RawData> found = new ArrayList<RawData>();
		for (Iterator<CalendarDates> iterator = allElements.iterator(); iterator
				.hasNext();) {
			CalendarDates element = iterator.next();
			if (element.getService_id().equals(lookupId)) {
				found.add(element);
			}
		}
		return found;
	}

	private List<RawData> findStopTimesByTripId(List<StopTimes> allElements,
			String lookupId) {
		List<RawData> found = new ArrayList<RawData>();
		for (Iterator<StopTimes> iterator = allElements.iterator(); iterator
				.hasNext();) {
			StopTimes element = iterator.next();
			if (element.getTrip_id().equals(lookupId)) {
				found.add(element);
				iterator.remove();
			}
		}
		return found;
	}

	private List<RawData> findStopByStopId(List<Stops> allElements,
			String lookupId) {
		List<RawData> found = new ArrayList<RawData>();
		for (Iterator<Stops> iterator = allElements.iterator(); iterator
				.hasNext();) {
			Stops element = iterator.next();
			if (element.getStop_id().equals(lookupId)) {
				found.add(element);
			}
		}
		return found;
	}
}
