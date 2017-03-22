package com.gtransit.raw.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gtransit.csv.annotations.CsvFieldOrdering;
import com.gtransit.graph.annotations.GraphProperty;

/**
 * This class represents a Bus Route
 *  
 *  @author Renato Barbosa
 *  
 * */
@Entity
@Table(name = "Routes", indexes = { @Index(name = "idxRoutes", columnList = "agency_id")})
public class Routes implements RawData{
	
	@Id
	@JsonProperty("routeId")
	@CsvFieldOrdering(position=0)
	private String route_id;
	
	@Column
	@CsvFieldOrdering(position=1)
	private String agency_id;

	@Column
	@GraphProperty("code")
	@CsvFieldOrdering(position=2)
	private String route_short_name;
	
	@Column
	@GraphProperty("longName")
	@CsvFieldOrdering(position=3)
	private String route_long_name;
	
	@Column
	@GraphProperty("description")
	@CsvFieldOrdering(position=4)
	private String route_desc;
	
	@Column
	@GraphProperty("type")
	@CsvFieldOrdering(position=5)
	private String route_type;
	
	@Column
	@GraphProperty("url")
	@CsvFieldOrdering(position=6)
	private String route_url;
	
	@Column
	@GraphProperty("routeColor")
	@CsvFieldOrdering(position=7)
	private String route_color;
	
	@Column
	@GraphProperty("routeTextColor")
	@CsvFieldOrdering(position=8)
	private String route_text_color;

	public String getRoute_id() {
		return route_id;
	}

	public void setRoute_id(String route_id) {
		this.route_id = route_id;
	}

	public String getAgency_id() {
		return agency_id;
	}

	public void setAgency_id(String agency_id) {
		this.agency_id = agency_id;
	}

	public String getRoute_short_name() {
		return route_short_name;
	}

	public void setRoute_short_name(String route_short_name) {
		this.route_short_name = route_short_name;
	}

	public String getRoute_long_name() {
		return route_long_name;
	}

	public void setRoute_long_name(String route_long_name) {
		this.route_long_name = route_long_name;
	}

	public String getRoute_desc() {
		return route_desc;
	}

	public void setRoute_desc(String route_desc) {
		this.route_desc = route_desc;
	}

	public String getRoute_type() {
		return route_type;
	}

	public void setRoute_type(String route_type) {
		this.route_type = route_type;
	}

	public String getRoute_url() {
		return route_url;
	}

	public void setRoute_url(String route_url) {
		this.route_url = route_url;
	}

	public String getRoute_color() {
		return route_color;
	}

	public void setRoute_color(String route_color) {
		this.route_color = route_color;
	}

	public String getRoute_text_color() {
		return route_text_color;
	}

	public void setRoute_text_color(String route_text_color) {
		this.route_text_color = route_text_color;
	}

	public String indentifier() {
		return this.route_id;
	}
	
	
}
