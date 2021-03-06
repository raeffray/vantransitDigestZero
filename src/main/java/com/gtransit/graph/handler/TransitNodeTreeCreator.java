package com.gtransit.graph.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import com.gtransit.commons.BeanPopulator;
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
import com.gtransit.raw.data.Service;
import com.gtransit.raw.data.StopTimes;
import com.gtransit.raw.data.Stops;
import com.gtransit.raw.data.Transfers;
import com.gtransit.raw.data.Trips;
import com.gtransit.reflection.ReflectionData;
import com.gtransit.relationaldb.RelationalDAO;

public class TransitNodeTreeCreator {

	static Logger logger = Logger.getLogger(TransitNodeTreeCreator.class);

	private static final String GRAPH_LABEL_KEY = "graph.label";

	private Collection<Agency> agencyList;

	private Collection<Transfers> transferList;

	private Collection<Stops> stopList;

	private Collection<Calendar> calendarList;

	private Collection<Service> serviceList;

	private Collection<CalendarDates> calendarDateList;

	Map<String, Long> serviceNodesIds;

	Map<String, Long> agencyNodesIds;

	Map<String, Long> serviceDatesNodesIds;

	Map<String, Long> stopsNodesIds;

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
			RelationalDAO.getInstance().serverShutdown();
			logger.info("Graph Stream Shutdown.");
			BatchInsertionGraphDB.getInstance().shutdown();

		}
	}

	public void loadCSVs() throws Exception {

		CSVReader reader = new CSVReader();

		if (agencyList == null) {

			logger.info("Loading [Agency]");
			agencyList = ReflectionData.getInstance().buildList(Agency.class, reader.readCSVForData(Agency.class));

			logger.info("Loading [Tranfers]");
			// transferList = ReflectionData.getInstance().buildList(
			// Transfers.class, reader.readCSVForData(Transfers.class));
			transferList = new ArrayList<Transfers>();

			logger.info("Loading [Stops]");
			stopList = ReflectionData.getInstance().buildList(Stops.class, reader.readCSVForData(Stops.class));

			logger.info("Loading [Calendar]");
			calendarList = ReflectionData.getInstance().buildList(Calendar.class,
					reader.readCSVForData(Calendar.class));

			logger.info("Loading [CalendarDates]");
			calendarDateList = ReflectionData.getInstance().buildList(CalendarDates.class,
					reader.readCSVForData(CalendarDates.class));

			// loading into HSSQLDB since these are the biggest data set
			logger.info("Loading [Routes]");
			RelationalDAO.getInstance().insertData(Routes.class);

			logger.info("Loading [Trips]");
			RelationalDAO.getInstance().insertData(Trips.class);

			logger.info("Loading [StopTimes]");
			RelationalDAO.getInstance().insertData(StopTimes.class);
		}
	}

	private void createBusStopsNode() throws Exception {

		logger.info("Creating [Stops] node");

		String[] serviceLabels = { Configuration.getConfigurationForClass(Stops.class).getString(GRAPH_LABEL_KEY) };

		List<RawData> rawList = new ArrayList<RawData>();

		rawList.addAll(stopList);

		stopsNodesIds = BatchInsertionGraphDB.getInstance().createNodes(serviceLabels, rawList, Stops.class, null);

		logger.info("Creating [Transfers] relationship");
		for (RawData rawData : transferList) {
			Transfers tranfers = (Transfers) rawData;
			long from = stopsNodesIds.get(tranfers.getFrom_stop_id());
			long to = stopsNodesIds.get(tranfers.getTo_stop_id());
			Map<String, Object> attributes = ReflectionData.getInstance().getFieldValue(rawData, Transfers.class);
			RelationshipDescriber relationship = new RelationshipDescriber(from, to,
					TransitRelationships.TRANSFER_TO.name(), attributes);
			BatchInsertionGraphDB.getInstance().createRelationships(relationship);
		}

	}

	private void createAgencyNode() throws Exception {

		logger.info("Creating [Agency] node");

		String[] serviceLabels = { Configuration.getConfigurationForClass(Agency.class).getString(GRAPH_LABEL_KEY) };

		List<RawData> rawList = new ArrayList<RawData>();

		rawList.addAll(agencyList);

		agencyNodesIds = BatchInsertionGraphDB.getInstance().createNodes(serviceLabels, rawList, Agency.class, null);

	}

	private void createSetRouteNode(String agencyId, long agencyNodeId) throws Exception {

		logger.info("Creating [Route] nodes");

		List<RawData> routesFromAgency = RelationalDAO.getInstance().findRoutesByAgencyId(agencyId);

		String[] childLabels = { Configuration.getConfigurationForClass(Routes.class).getString(GRAPH_LABEL_KEY) };

		RelationshipDescriber relationship = new RelationshipDescriber(agencyNodeId, -1,
				TransitRelationships.OPERATES.name(), null);

		Map<String, Long> routeNodesIds = BatchInsertionGraphDB.getInstance().createNodeStructure(routesFromAgency,
				childLabels, relationship, Routes.class);

		for (RawData rawRoute : routesFromAgency) {
			Routes route = (Routes) rawRoute;
			logger.info("Creating [Route] code [" + route.getRoute_short_name() + "]  node");
			Long routeFather = routeNodesIds.get(route.getRoute_id());
			createSetTripNode(route.getRoute_id(), routeFather);
		}
	}

	private void createSetTripNode(String routeId, long routeNodeId) throws Exception {

		logger.info("Creating [Trips] nodes");

		List<RawData> tripsFromRoute = RelationalDAO.getInstance().findTripsByRouteId(routeId);

		String[] childLabels = { Configuration.getConfigurationForClass(Trips.class).getString(GRAPH_LABEL_KEY) };

		RelationshipDescriber relationShipTravels = new RelationshipDescriber(routeNodeId, -1,
				TransitRelationships.TRAVELS.name(), null);

		Map<String, Long> tripsNodesIds = BatchInsertionGraphDB.getInstance().createNodeStructure(tripsFromRoute,
				childLabels, relationShipTravels, Trips.class);

		for (RawData rawData : tripsFromRoute) {

			Trips trip = (Trips) rawData;

			logger.info("Creating stop relatioships to [Trip] tripId [" + trip.getTrip_id() + "]");

			String tripId = trip.getTrip_id();

			int daysInServiceAsInteger = getDaysInServiceAsInteger(trip.getService_id());

			trip.setDaysOfWeekInService(daysInServiceAsInteger);

			Collection<RawData> servicesByServiceId = findServicesByServiceId(serviceList, trip.getService_id());

			for (RawData data : servicesByServiceId) {
				Service service = (Service) data;
				RelationshipDescriber relationShipExecute = new RelationshipDescriber(tripsNodesIds.get(tripId),
						serviceNodesIds.get(String.valueOf(service.getId())), TransitRelationships.EXECUTE.name(),
						null);
				BatchInsertionGraphDB.getInstance().createRelationships(relationShipExecute);
			}

			List<RelationshipDescriber> stopTimesRelationships = createSetStopTimesRelationship(tripId,
					tripsNodesIds.get(tripId), TransitRelationships.PICKUP_AT.name());

			for (RelationshipDescriber relationshipDesc : stopTimesRelationships) {
				BatchInsertionGraphDB.getInstance().createRelationships(relationshipDesc);
			}
		}
	}

	private void createSetServiceNode() throws Exception {

		logger.info("Creating [Service] nodes");

		String[] serviceLabels = { Configuration.getConfigurationForClass(Service.class).getString(GRAPH_LABEL_KEY) };

		serviceList = BeanPopulator.createServicesForDayInWeek(calendarList);

		List<RawData> rawList = new ArrayList<RawData>();

		rawList.addAll(serviceList);

		serviceNodesIds = BatchInsertionGraphDB.getInstance().createNodes(serviceLabels, rawList, Service.class, null);

		for (Service service : serviceList) {
			Long serviceNodeId = serviceNodesIds.get(String.valueOf(service.getId()));
			createSetServiceDateNode(service.getService_id(), serviceNodeId);
		}

	}

	private void createSetServiceDateNode(String serviceId, long serviceNodeId) throws Exception {

		Collection<RawData> calendarDatesFromTrip = findCalendarDateByServiceId(calendarDateList, serviceId);

		String[] childLabels = {
				Configuration.getConfigurationForClass(CalendarDates.class).getString(GRAPH_LABEL_KEY) };

		RelationshipDescriber relationship = new RelationshipDescriber(serviceNodeId, -1,
				TransitRelationships.SERVICE_DATE_RESTRICED_IN.name(), null);

		BatchInsertionGraphDB.getInstance().createNodeStructure(calendarDatesFromTrip, childLabels, relationship,
				CalendarDates.class);

	}

	private List<RelationshipDescriber> createSetStopTimesRelationship(String tripId, long tripNodeId, String type)
			throws Exception {

		logger.debug("Creating [StopTimes] relationships");

		List<RelationshipDescriber> relationships = new ArrayList<RelationshipDescriber>();

		List<RawData> stopsTimesFromTrip = RelationalDAO.getInstance().findStopTimesByTripId(tripId);
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
			RelationshipDescriber relationship = new RelationshipDescriber(tripNodeId, stopsNodesIds.get(stopId), type,
					attributes);
			relationships.add(relationship);
		}

		return relationships;
	}

	private Collection<RawData> findCalendarDateByServiceId(Collection<CalendarDates> allElements, String lookupId) {
		Collection<RawData> found = new HashSet<RawData>();
		for (Iterator<CalendarDates> iterator = allElements.iterator(); iterator.hasNext();) {
			CalendarDates element = iterator.next();
			if (element.getService_id().equals(lookupId)) {
				found.add(element);
			}
		}
		return found;
	}

	private Calendar findCalendarByServiceId(String lookupId) {
		for (Iterator<Calendar> iterator = calendarList.iterator(); iterator.hasNext();) {
			Calendar element = iterator.next();
			if (element.getService_id().equals(lookupId)) {
				return element;
			}
		}
		return null;
	}

	private Collection<RawData> findServicesByServiceId(Collection<Service> allElements, String lookupId) {
		List<RawData> found = new ArrayList<RawData>();
		for (Iterator<Service> iterator = allElements.iterator(); iterator.hasNext();) {
			Service element = iterator.next();
			if (element.getService_id().equals(lookupId)) {
				found.add(element);
			}
		}
		return found;
	}

	private int getDaysInServiceAsInteger(String serviceId) {

		Calendar calendar = findCalendarByServiceId(serviceId);

		StringBuilder builder = new StringBuilder();

		builder.append(calendar.getMonday());
		builder.append(calendar.getTuesday());
		builder.append(calendar.getWednesday());
		builder.append(calendar.getThursday());
		builder.append(calendar.getFriday());
		builder.append(calendar.getSaturday());
		builder.append(calendar.getSunday());

		return Integer.parseInt(builder.toString(), 2);
	}

}
