package com.raeffray.graph.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.raeffray.csv.CSVReader;
import com.raeffray.graph.RelationshipDescriber;
import com.raeffray.raw.data.Agency;
import com.raeffray.raw.data.Calendar;
import com.raeffray.raw.data.CalendarDates;
import com.raeffray.raw.data.RawData;
import com.raeffray.raw.data.Routes;
import com.raeffray.raw.data.StopTimes;
import com.raeffray.raw.data.Stops;
import com.raeffray.raw.data.Trips;
import com.raeffray.reflection.ReflectionData;
import com.raeffray.rest.cient.RestClient;

public class RouteNodeHandler {

	static Logger logger = Logger.getLogger(RestClient.class);

	private List<Agency> agencyList;

	//private List<Transfers> TransferList;

	private List<Routes> routeList;

	private List<Trips> tripList;

	//private List<Shapes> shapeList;

	private List<Stops> stopList;

	private List<Calendar> serviceList;

	private List<CalendarDates> calendarDateList;

	private List<StopTimes> stopTimesList;

	Map<String, Long> serviceNodesIds;

	Map<String, Long> agencyNodesIds;

	Map<String, Long> serviceDatesNodesIds;

	Map<String, Long> stopsNodesIds;

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

			createBusStopsNode();

			for (Agency agency : agencyList) {
				Long agencyNodeId = agencyNodesIds.get(agency.getAgency_id());
				createSetRouteNode(agency.getAgency_id(), agencyNodeId);
			}

		} catch (Exception e) {
			logger.error(e);
		}
	}

	private void createBusStopsNode() throws Exception {

		logger.info("Creating [Stops] node");

		String[] serviceLabels = { "BUSSTOP", "TEST" };

		List<RawData> rawList = new ArrayList<RawData>();

		rawList.addAll(stopList);

		JSONArray responseNodes = RestClient.getInstance().createNodes(
				serviceLabels, rawList);

		stopsNodesIds = extractCreatedStopsNodes(responseNodes);

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
			logger.info("Creating [Route] code [" + route.getRoute_short_name()
					+ "]  node");
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

		Map<String, Collection<RelationshipDescriber>> stopMap = new HashMap<String, Collection<RelationshipDescriber>>();

		for (RawData rawData : tripsFromRoute) {
			Trips trip = (Trips) rawData;
			logger.info("Creating stop relatioships to [Trip] tripId ["
					+ trip.getTrip_id() + "]");
			String tripId = trip.getTrip_id();
			stopMap.put(tripId, createSetStopTimesRelationship(tripId));
		}

		RestClient.getInstance().createNodeStructureForTrip(tripsFromRoute,
				childLabels, relDesc, serviceDesc, serviceNodesIds,
				routeNodeId, stopMap);

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
		RestClient.getInstance().createNodeStructure(calendarDatesFromTrip,
				childLabels, relDesc, serviceNodeId);

	}

	private Collection<RelationshipDescriber> createSetStopTimesRelationship(
			String tripId) throws Exception {

		logger.debug("Creating [StopTimes] relationship describers");

		List<RawData> stopsTimesFromTrip = findStopTimesByTripId(stopTimesList,
				tripId);

		Collection<RelationshipDescriber> describers = new ArrayList<RelationshipDescriber>();

		for (RawData rawData : stopsTimesFromTrip) {
			StopTimes stopTime = (StopTimes) rawData;
			String stopId = stopTime.getStop_id();
			RelationshipDescriber describer = new RelationshipDescriber(
					"PICKUP_AT");
			describer.addAttribute("arrivalTime", stopTime.getArrival_time());
			describer.addAttribute("departureTime",
					stopTime.getDeparture_time());
			describer.addAttribute("dropoffType", stopTime.getDrop_off_type());
			describer.addAttribute("pickupType", stopTime.getPickup_type());
			describer.addAttribute("distTraveled",
					stopTime.getShape_dist_traveled());
			describer.addAttribute("headsign", stopTime.getStop_headsign());
			describer.addAttribute("sequence", stopTime.getStop_sequence());
			describer.addAttribute("stopNodeId", stopsNodesIds.get(stopId));
			describers.add(describer);
		}

		return describers;
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

	private Map<String, Long> extractCreatedStopsNodes(
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

}
