package com.gtransit.raw.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import com.gtransit.csv.annotations.CsvFieldOrdering;
import com.gtransit.csv.annotations.CsvIgnore;
import com.gtransit.graph.annotations.GraphProperty;

/**
 * @author i842771
 *
 */
@Entity
@Table(name = "Trips", indexes = { @Index(name = "idxTrips", columnList = "trip_id")})
public class Trips implements RawData {
	
	@Column
	@CsvFieldOrdering(position=0)
	private String route_id;
	
	@Column
	@CsvFieldOrdering(position=1)
	private String service_id;
	
	@Column
	@Id
	@GraphProperty("tripId")
	@CsvFieldOrdering(position=2)
	private String trip_id;
	
	@Column
	@GraphProperty("headsign")
	@CsvFieldOrdering(position=3)
	private String trip_headsign;
	
	@Column
	@GraphProperty("shortName")
	@CsvFieldOrdering(position=4)
	private String trip_short_name;
	
	@Column
	@GraphProperty("directionId")
	@CsvFieldOrdering(position=5)
	private String direction_id;
	
	@Column
	@GraphProperty("blockId")
	@CsvFieldOrdering(position=6)
	private String block_id;
	
	@Column
	@GraphProperty("shapeId")
	@CsvFieldOrdering(position=7)
	private String shape_id;
	
	@Column
	@GraphProperty("wheelchairAccessible")
	@CsvFieldOrdering(position=8)
	private String wheelchair_accessible;
	
	@Column
	@GraphProperty("bikesAllowed")
	@CsvFieldOrdering(position=9)
	private String bikes_allowed;
	
	
	@CsvIgnore
	private int daysOfWeekInService;
	
	public String getRoute_id() {
		return route_id;
	}

	public void setRoute_id(String route_id) {
		this.route_id = route_id;
	}

	public String getService_id() {
		return service_id;
	}

	public void setService_id(String service_id) {
		this.service_id = service_id;
	}

	public String getTrip_id() {
		return trip_id;
	}

	public void setTrip_id(String trip_id) {
		this.trip_id = trip_id;
	}

	public String getTrip_headsign() {
		return trip_headsign;
	}

	public void setTrip_headsign(String trip_headsign) {
		this.trip_headsign = trip_headsign;
	}

	public String getTrip_short_name() {
		return trip_short_name;
	}

	public void setTrip_short_name(String trip_short_name) {
		this.trip_short_name = trip_short_name;
	}

	public String getDirection_id() {
		return direction_id;
	}

	public void setDirection_id(String direction_id) {
		this.direction_id = direction_id;
	}

	public String getBlock_id() {
		return block_id;
	}

	public void setBlock_id(String block_id) {
		this.block_id = block_id;
	}

	public String getShape_id() {
		return shape_id;
	}

	public void setShape_id(String shape_id) {
		this.shape_id = shape_id;
	}

	public String indentifier() {
		return this.trip_id;
	}

	public int getDaysOfWeekInService() {
		return daysOfWeekInService;
	}

	public void setDaysOfWeekInService(int daysOfWeekInService) {
		this.daysOfWeekInService = daysOfWeekInService;
	}

	public String getWheelchair_accessible() {
		return wheelchair_accessible;
	}

	public void setWheelchair_accessible(String wheelchair_accessible) {
		this.wheelchair_accessible = wheelchair_accessible;
	}

	public String getBikes_allowed() {
		return bikes_allowed;
	}

	public void setBikes_allowed(String bikes_allowed) {
		this.bikes_allowed = bikes_allowed;
	}
	
	
}
