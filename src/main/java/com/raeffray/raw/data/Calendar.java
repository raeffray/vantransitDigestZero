package com.raeffray.raw.data;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.raeffray.graph.annotations.GraphProperty;

public class Calendar implements RawData {

	@GraphProperty("serviceId")
	private String service_id;
	
	@GraphProperty("monday")
	private String monday;
	
	@GraphProperty("tuesday")
	private String tuesday;
	
	@GraphProperty("wednesday")
	private String wednesday;
	
	@GraphProperty("thursday")
	private String thursday;
	
	@GraphProperty("friday")
	private String friday;
	
	@GraphProperty("saturday")
	private String saturday;
	
	@GraphProperty("sunday")
	private String sunday;

	@GraphProperty("startDate")
	private String start_date;

	@GraphProperty("endDate")
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

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="PST")
	public Date getStartDate() {
		try {
			SimpleDateFormat dt = new SimpleDateFormat("yyyyMMdd");
			return dt.parse(this.start_date);	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="PST")
	public Date getEndDate() {
		try {
			SimpleDateFormat dt = new SimpleDateFormat("yyyyMMdd");
			return dt.parse(this.end_date);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}

	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}

	public String indentifier() {
		return this.service_id;
	}

}
