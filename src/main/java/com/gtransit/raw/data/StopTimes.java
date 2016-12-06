package com.gtransit.raw.data;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import com.gtransit.csv.annotations.CsvFieldOrdering;
import com.gtransit.graph.annotations.GraphProperty;

@Entity
@Table(name = "StopTimes", indexes = { @Index(name = "idxStopTimes", columnList = "trip_id")})
public class StopTimes implements RawData, Serializable {

	@Id
	@GraphProperty("tripId")
	@CsvFieldOrdering(position=0)
	private String trip_id;
	
	@Column
	@GraphProperty("arrivalTime")
	@CsvFieldOrdering(position=1)
	private String arrival_time;
	
	@Column
	@GraphProperty("departureTime")
	@CsvFieldOrdering(position=2)
	private String departure_time; 
	
	@Column
	@Id
	@GraphProperty("stopId")
	@CsvFieldOrdering(position=3)
	private String stop_id;
	
	@Column
	@Id
	@GraphProperty("sequence")
	@CsvFieldOrdering(position=4)
	private String stop_sequence;
	
	@Column
	@GraphProperty("stopHeadsign")
	@CsvFieldOrdering(position=5)
	private String stop_headsign; 
	
	@Column
	@GraphProperty("pickupType")
	@CsvFieldOrdering(position=6)
	private String pickup_type;
	
	@Column
	@GraphProperty("dropoffType")
	@CsvFieldOrdering(position=7)
	private String drop_off_type;
	
	@Column
	@GraphProperty("shapeDistTraveled")
	@CsvFieldOrdering(position=8)
	private String shape_dist_traveled;
	
	@Column
	@GraphProperty("timepoint")
	@CsvFieldOrdering(position=9)
	private String timepoint;

	public String getTrip_id() {
		return trip_id;
	}

	public void setTrip_id(String trip_id) {
		this.trip_id = trip_id;
	}

	public String getArrival_time() {
		return arrival_time;
	}

	public void setArrival_time(String arrival_time) {
		this.arrival_time = arrival_time;
	}

	public String getDeparture_time() {
		return departure_time;
	}

	public void setDeparture_time(String departure_time) {
		this.departure_time = departure_time;
	}

	public String getStop_id() {
		return stop_id;
	}

	public void setStop_id(String stop_id) {
		this.stop_id = stop_id;
	}

	public String getStop_sequence() {
		return stop_sequence;
	}

	public void setStop_sequence(String stop_sequence) {
		this.stop_sequence = stop_sequence;
	}

	public String getStop_headsign() {
		return stop_headsign;
	}

	public void setStop_headsign(String stop_headsign) {
		this.stop_headsign = stop_headsign;
	}

	public String getPickup_type() {
		return pickup_type;
	}

	public void setPickup_type(String pickup_type) {
		this.pickup_type = pickup_type;
	}

	public String getDrop_off_type() {
		return drop_off_type;
	}

	public void setDrop_off_type(String drop_off_type) {
		this.drop_off_type = drop_off_type;
	}

	public String getShape_dist_traveled() {
		return shape_dist_traveled;
	}

	public void setShape_dist_traveled(String shape_dist_traveled) {
		this.shape_dist_traveled = shape_dist_traveled;
	}

	public String indentifier() {
		return null;
	}

	public String getTimepoint() {
		return timepoint;
	}

	public void setTimepoint(String timepoint) {
		this.timepoint = timepoint;
	}
	

}
