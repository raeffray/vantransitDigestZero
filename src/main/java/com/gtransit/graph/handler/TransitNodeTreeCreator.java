package com.gtransit.graph.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.DynamicRelationshipType;

import com.gtransit.commons.Configuration;
import com.gtransit.csv.CSVReader;
import com.gtransit.graph.RelationshipDescriber;
import com.gtransit.graph.TransitRelationships;
import com.gtransit.graph.inserter.BatchInsertionGraphDB;
import com.gtransit.raw.data.Agency;
import com.gtransit.raw.data.Calendar;
import com.gtransit.raw.data.CalendarDates;
import com.gtransit.raw.data.RawData;
import com.gtransit.raw.data.Routes;
import com.gtransit.raw.data.StopTimes;
import com.gtransit.raw.data.Stops;
import com.gtransit.raw.data.Transfers;
import com.gtransit.raw.data.Trips;
import com.gtransit.reflection.ReflectionData;
import com.gtransit.relationaldb.DbServer;

public class TransitNodeTreeCreator {

	static Logger logger = Logger.getLogger(TransitNodeTreeCreator.class);

	private static final String GRAPH_LABEL_KEY = "graph.label";

	private List<Agency> agencyList;

	private List<Transfers> transferList;

	private List<Routes> routeList;

	private List<Trips> tripList;

	// private List<Shapes> shapeList;

	private List<Stops> stopList;

	private List<Calendar> serviceList;

	private List<CalendarDates> calendarDateList;

	Map<String, Long> serviceNodesIds;

	Map<String, Long> agencyNodesIds;

	Map<String, Long> serviceDatesNodesIds;

	Map<String, Long> stopsNodesIds;

	public void loadCSVs() throws Exception {

		CSVReader reader = new CSVReader();

		if (agencyList == null) {

			logger.info("Loading [Agency]");
			agencyList = ReflectionData.getInstance().buildList(Agency.class,
					reader.readCSVForData(Agency.class));

			logger.info("Loading [Routes]");
			routeList = ReflectionData.getInstance().buildList(Routes.class,
					reader.readCSVForData(Routes.class));

			logger.info("Loading [Trips]");
			tripList = ReflectionData.getInstance().buildList(Trips.class,
					reader.readCSVForData(Trips.class));
			
			logger.info("Loading [Tranfers]");
			transferList = ReflectionData.getInstance().buildList(Transfers.class,
					reader.readCSVForData(Transfers.class));

			logger.info("Loading [Stops]");
			stopList = ReflectionData.getInstance().buildList(Stops.class,
					reader.readCSVForData(Stops.class));

			logger.info("Loading [StopTimes]");
			DbServer.getInstance().loadRelationalDB();

			logger.info("Loading [Calendar]");
			serviceList = ReflectionData.getInstance().buildList(
					Calendar.class, reader.readCSVForData(Calendar.class));

			logger.info("Loading [CalendarDates]");
			calendarDateList = ReflectionData.getInstance().buildList(
					CalendarDates.class,
					reader.readCSVForData(CalendarDates.class));

			// List<Transfers> TransferList = ReflectionData.getInstance()
			// .buildList(Transfers.class,
			// reader.readCSVForData(Transfers.class));

			// List<Shapes> shapeList = ReflectionData.getInstance().buildList(
			// Shapes.class, reader.readCSVForData(Shapes.class));
		}
	}

	public void create() throws Exception {
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
		} finally {
			logger.info("HSQLDB Shutdown.");
			DbServer.getInstance().serverShutdown();
			logger.info("Graph Stream Shutdown.");
			BatchInsertionGraphDB.getInstance().shutdown();
			
		}
	}

	private void createBusStopsNode() throws Exception {

		logger.info("Creating [Stops] node");

		String[] serviceLabels = { Configuration.getConfigurationForClass(
				Stops.class).getString(GRAPH_LABEL_KEY) };

		List<RawData> rawList = new ArrayList<RawData>();

		rawList.addAll(stopList);

		stopsNodesIds = BatchInsertionGraphDB.getInstance().createNodes(
				serviceLabels, rawList, Stops.class, null);
		
		logger.info("Creating [Transfers] relationship");
		for (RawData rawData : transferList) {
			Transfers tranfers = (Transfers) rawData;
			long from = stopsNodesIds.get(tranfers.getFrom_stop_id());
			long to = stopsNodesIds.get(tranfers.getTo_stop_id());
			Map<String, Object> attributes = ReflectionData.getInstance().getFieldValue(rawData, Transfers.class);
			RelationshipDescriber relationship = new RelationshipDescriber(from, to, TransitRelationships.TRANSFER_TO.name(), attributes);
			BatchInsertionGraphDB.getInstance().createRelationships(relationship);
		}
		
		
	}

	private void createAgencyNode() throws Exception {

		logger.info("Creating [Agency] node");

		String[] serviceLabels = { Configuration.getConfigurationForClass(
				Agency.class).getString(GRAPH_LABEL_KEY) };

		List<RawData> rawList = new ArrayList<RawData>();

		rawList.addAll(agencyList);

		agencyNodesIds = BatchInsertionGraphDB.getInstance().createNodes(
				serviceLabels, rawList, Agency.class, null);

	}

	private void createSetRouteNode(String agencyId, long agencyNodeId)
			throws Exception {

		logger.info("Creating [Route] nodes");

		List<RawData> routesFromAgency = findRouteByAgencyId(routeList,
				agencyId);

		String[] childLabels = { Configuration.getConfigurationForClass(
				Routes.class).getString(GRAPH_LABEL_KEY) };

		RelationshipDescriber relationship = new RelationshipDescriber(
				agencyNodeId, -1, TransitRelationships.OPERATES.name(), null);

		Map<String, Long> routeNodesIds = BatchInsertionGraphDB.getInstance()
				.createNodeStructure(routesFromAgency, childLabels,
						relationship, Routes.class);

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

		String[] childLabels = { Configuration.getConfigurationForClass(
				Trips.class).getString(GRAPH_LABEL_KEY) };

		RelationshipDescriber relationShipTravels = new RelationshipDescriber(
				routeNodeId, -1, TransitRelationships.TRAVELS.name(), null);

		Map<String, Long> tripsNodesIds = BatchInsertionGraphDB.getInstance()
				.createNodeStructure(tripsFromRoute, childLabels,
						relationShipTravels, Trips.class);

		for (RawData rawData : tripsFromRoute) {
			Trips trip = (Trips) rawData;
			logger.info("Creating stop relatioships to [Trip] tripId ["
					+ trip.getTrip_id() + "]");
			String tripId = trip.getTrip_id();

			RelationshipDescriber relationShipExecute = new RelationshipDescriber(
					tripsNodesIds.get(tripId), serviceNodesIds.get(trip
							.getService_id()),
					TransitRelationships.EXECUTE.name(), null);

			BatchInsertionGraphDB.getInstance().createRelationships(
					relationShipExecute);

			List<RelationshipDescriber> stopTimesRelationships = createSetStopTimesRelationship(
					tripId, tripsNodesIds.get(tripId),
					TransitRelationships.PICKUP_AT.name());

			for (RelationshipDescriber relationshipDesc : stopTimesRelationships) {
				BatchInsertionGraphDB.getInstance().createRelationships(
						relationshipDesc);
			}
		}
	}

	private void createSetServiceNode() throws Exception {

		logger.info("Creating [Service] nodes");

		String[] serviceLabels = { Configuration.getConfigurationForClass(
				Calendar.class).getString(GRAPH_LABEL_KEY) };

		List<RawData> rawList = new ArrayList<RawData>();

		rawList.addAll(serviceList);

		serviceNodesIds = BatchInsertionGraphDB.getInstance().createNodes(
				serviceLabels, rawList, Calendar.class, null);

		for (Calendar service : serviceList) {
			Long serviceNodeId = serviceNodesIds.get(service.getService_id());
			createSetServiceDateNode(service.getService_id(), serviceNodeId);
		}

	}

	private void createSetServiceDateNode(String serviceId, long serviceNodeId)
			throws Exception {

		List<RawData> calendarDatesFromTrip = findCalendarDateByServiceId(
				calendarDateList, serviceId);

		String[] childLabels = { Configuration.getConfigurationForClass(
				CalendarDates.class).getString(GRAPH_LABEL_KEY) };

		RelationshipDescriber relationship = new RelationshipDescriber(
				serviceNodeId, -1,
				TransitRelationships.SERVICE_DATE_RESTRICED_IN.name(), null);

		BatchInsertionGraphDB.getInstance().createNodeStructure(
				calendarDatesFromTrip, childLabels, relationship,
				CalendarDates.class);

	}

	private List<RelationshipDescriber> createSetStopTimesRelationship(
			String tripId, long tripNodeId, String type) throws Exception {

		logger.debug("Creating [StopTimes] relationships");

		List<RelationshipDescriber> relationships = new ArrayList<RelationshipDescriber>();

		List<StopTimes> stopsTimesFromTrip = DbServer.getInstance().getStopTimesByTripId(tripId);
		for (RawData rawData : stopsTimesFromTrip) {
			StopTimes stopTime = (StopTimes) rawData;
			String stopId = stopTime.getStop_id();
			Map<String, Object> attributes = new HashMap<String, Object>();
			attributes.put("arrivalTime", stopTime.getArrival_time());
			attributes.put("departureTime", stopTime.getDeparture_time());
			attributes.put("dropoffType", stopTime.getDrop_off_type());
			attributes.put("pickupType", stopTime.getPickup_type());
			attributes.put("distTraveled", stopTime.getShape_dist_traveled());
			attributes.put("headsign", stopTime.getStop_headsign());
			attributes.put("sequence", stopTime.getStop_sequence());
			attributes.put("stopNodeId", stopsNodesIds.get(stopId));
			RelationshipDescriber relationship = new RelationshipDescriber(
					tripNodeId, stopsNodesIds.get(stopId), type, attributes);
			relationships.add(relationship);
		}

		return relationships;
	}

	private List<RawData> findTripById(List<Trips> allTrips, String routeId) {
		List<RawData> foundTrips = new ArrayList<RawData>();
		for (Iterator<Trips> iterator = allTrips.iterator(); iterator.hasNext();) {
			Trips trip = iterator.next();
			if (trip.getRoute_id().equals(routeId)) {
				foundTrips.add(trip);
				iterator.remove();
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
