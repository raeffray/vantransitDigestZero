package com.gtransit.raw.data;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gtransit.csv.annotations.CsvFieldOrdering;
import com.gtransit.graph.annotations.GraphProperty;

public class Calendar implements RawData {

	@GraphProperty("serviceId")
	@CsvFieldOrdering(position=0)
	private String service_id;
	
	@GraphProperty("monday")
	@CsvFieldOrdering(position=1)
	private String monday;
	
	@GraphProperty("tuesday")
	@CsvFieldOrdering(position=2)
	private String tuesday;
	
	@GraphProperty("wednesday")
	@CsvFieldOrdering(position=3)
	private String wednesday;
	
	@GraphProperty("thursday")
	@CsvFieldOrdering(position=4)
	private String thursday;
	
	@GraphProperty("friday")
	@CsvFieldOrdering(position=5)
	private String friday;
	
	@GraphProperty("saturday")
	@CsvFieldOrdering(position=6)
	private String saturday;
	
	@GraphProperty("sunday")
	@CsvFieldOrdering(position=7)
	private String sunday;

	@GraphProperty("startDate")
	@CsvFieldOrdering(position=8)
	private String start_date;

	@GraphProperty("endDate")
	@CsvFieldOrdering(position=9)
	private String end_date;

	public String getService_id() {
		return service_id;
	}

	public void setService_id(String service_id) {
		this.service_id = service_id;
	}

	public String getMonday() {
		return monday;
	}

	public void setMonday(String monday) {
		this.monday = monday;
	}

	public String getTuesday() {
		return tuesday;
	}

	public void setTuesday(String tuesday) {
		this.tuesday = tuesday;
	}

	public String getWednesday() {
		return wednesday;
	}

	public void setWednesday(String wednesday) {
		this.wednesday = wednesday;
	}

	public String getThursday() {
		return thursday;
	}

	public void setThursday(String thursday) {
		this.thursday = thursday;
	}

	public String getFriday() {
		return friday;
	}

	public void setFriday(String friday) {
		this.friday = friday;
	}

	public String getSaturday() {
		return saturday;
	}

	public void setSaturday(String saturday) {
		this.saturday = saturday;
	}

	public String getSunday() {
		return sunday;
	}

	public void setSunday(String sunday) {
		this.sunday = sunday;
	}

	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}
 
	
 
	public String getStart_date() {
		return start_date;
	}

	public String getEnd_date() {
		return end_date;
	}

	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}

	public String indentifier() {
		return this.service_id;
	}

}
