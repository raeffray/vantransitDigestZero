package com.gtransit.raw.data;

import com.gtransit.csv.annotations.CsvFieldOrdering;
import com.gtransit.graph.annotations.GraphProperty;

public class Shapes implements RawData {
	
	@GraphProperty("shapeId")
	@CsvFieldOrdering(position=0)
	private String shape_id; 
	
	@GraphProperty("latitude")
	@CsvFieldOrdering(position=1)
	private String shape_pt_lat;
	
	@GraphProperty("longitude")
	@CsvFieldOrdering(position=2)
	private String shape_pt_lon;
	
	@GraphProperty("sequence")
	@CsvFieldOrdering(position=3)
	private String shape_pt_sequence;
	
	@GraphProperty("traveldDistance")
	@CsvFieldOrdering(position=4)
	private String shape_dist_traveled;

	public String getShape_id() {
		return shape_id;
	}

	public void setShape_id(String shape_id) {
		this.shape_id = shape_id;
	}

	public String getShape_pt_lat() {
		return shape_pt_lat;
	}

	public void setShape_pt_lat(String shape_pt_lat) {
		this.shape_pt_lat = shape_pt_lat;
	}

	public String getShape_pt_lon() {
		return shape_pt_lon;
	}

	public void setShape_pt_lon(String shape_pt_lon) {
		this.shape_pt_lon = shape_pt_lon;
	}

	public String getShape_pt_sequence() {
		return shape_pt_sequence;
	}

	public void setShape_pt_sequence(String shape_pt_sequence) {
		this.shape_pt_sequence = shape_pt_sequence;
	}

	public String getShape_dist_traveled() {
		return shape_dist_traveled;
	}

	public void setShape_dist_traveled(String shape_dist_traveled) {
		this.shape_dist_traveled = shape_dist_traveled;
	}

	public String indentifier() {
		return this.shape_id;
	}
	
	

}
