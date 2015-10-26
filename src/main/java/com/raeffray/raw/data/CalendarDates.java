package com.raeffray.raw.data;

import com.raeffray.graph.annotations.GraphProperty;

public class CalendarDates implements RawData {
	
	@GraphProperty("serviceId")
	private String service_id;
	
	@GraphProperty("date")
	private String date;
	
	@GraphProperty("exceptionType")
	private String exception_type;
	
	public String getService_id() {
		return service_id;
	}
	public void setService_id(String service_id) {
		this.service_id = service_id;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getException_type() {
		return exception_type;
	}
	public void setException_type(String exception_type) {
		this.exception_type = exception_type;
	}
	public String indentifier() {
		return null;
	}
	
	

}
