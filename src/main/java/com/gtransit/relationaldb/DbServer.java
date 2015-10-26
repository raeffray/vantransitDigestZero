package com.gtransit.relationaldb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hsqldb.Server;

import com.gtransit.csv.CSVReader;
import com.gtransit.raw.data.StopTimes;
import com.gtransit.reflection.ReflectionData;

public class DbServer {

	CSVReader reader = new CSVReader();

	static Logger logger = Logger.getLogger(DbServer.class);

	private static DbServer instance;

	private Connection connection = null;

	Server hsqlServer = null;

	private DbServer() throws Exception {

		Class.forName("org.hsqldb.jdbcDriver");

		connection = DriverManager.getConnection("jdbc:hsqldb:mem:reltransit",
				"sa", "");

		hsqlServer = new Server();
		hsqlServer.setLogWriter(null);
		hsqlServer.setSilent(true);
		hsqlServer.setDatabaseName(0, "reltransit");
		hsqlServer.start();
	}

	public static DbServer getInstance() throws Exception {
		if (instance == null) {
			try {
				instance = new DbServer();
			} catch (SQLException e) {
				logger.error(e);
			}
			return instance;
		}
		return instance;
	}

	private List<StopTimes> stopTimesList;

	public void loadRelationalDB() throws Exception {

		logger.info("Loading [StopTimes]");

		if (stopTimesList == null) {
			stopTimesList = ReflectionData.getInstance().buildList(
					StopTimes.class, reader.readCSVForData(StopTimes.class));
		}

		try {
			connection.prepareStatement("drop table stoptimes if exists;")
					.execute();
			connection
					.prepareStatement(
							"create memory table stoptimes (trip_id varchar(20), arrival_time varchar(20), departure_time varchar(20), stop_id varchar(20), stop_sequence varchar(20), stop_headsign varchar(20), pickup_type varchar(20), drop_off_type varchar(20), shape_dist_traveled varchar(20));")
					.execute();

			connection.prepareStatement(
					"create index idx on stoptimes (trip_id);").execute();

			PreparedStatement pstmt = connection
					.prepareStatement("insert into stoptimes (trip_id, arrival_time, departure_time, stop_id, stop_sequence, stop_headsign, pickup_type, drop_off_type, shape_dist_traveled)"
							+ "values (?,?,?,?,?,?,?,?,?);");

			for (StopTimes stopTimes : stopTimesList) {
				pstmt.setString(1, stopTimes.getTrip_id());
				pstmt.setString(2, stopTimes.getArrival_time());
				pstmt.setString(3, stopTimes.getDeparture_time());
				pstmt.setString(4, stopTimes.getStop_id());
				pstmt.setString(5, stopTimes.getStop_sequence());
				pstmt.setString(6, stopTimes.getStop_headsign());
				pstmt.setString(7, stopTimes.getPickup_type());
				pstmt.setString(8, stopTimes.getDrop_off_type());
				pstmt.setString(9, stopTimes.getShape_dist_traveled());
				pstmt.execute();
				pstmt.clearParameters();
			}
			pstmt.execute();
						
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}

	public List<StopTimes> getStopTimesByTripId(String tripId) throws Exception {
		List<StopTimes> stopTimes = new ArrayList<StopTimes>();

		PreparedStatement ps = connection
				.prepareStatement("select * from stoptimes where trip_id = ?;");
		ps.setString(1, tripId);

		ResultSet rs = ps.executeQuery();
		rs.next();

		while (rs.next()) {
			StopTimes stopTime = new StopTimes();
			stopTime.setTrip_id(rs.getString(1));
			stopTime.setArrival_time(rs.getString(2));
			stopTime.setDeparture_time(rs.getString(3));
			stopTime.setStop_id(rs.getString(4));
			stopTime.setStop_sequence(rs.getString(5));
			stopTime.setStop_headsign(rs.getString(6));
			stopTime.setPickup_type(rs.getString(7));
			stopTime.setDrop_off_type(rs.getString(8));
			stopTime.setShape_dist_traveled(rs.getString(9));
			stopTimes.add(stopTime);
		}

		return stopTimes;
	}

	public void serverShutdown() {
		hsqlServer.stop();
		hsqlServer = null;
	}

}
