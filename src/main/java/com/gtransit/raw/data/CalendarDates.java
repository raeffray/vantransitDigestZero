package com.gtransit.raw.data;

import com.gtransit.csv.annotations.CsvFieldOrdering;
import com.gtransit.graph.annotations.GraphProperty;

public class CalendarDates implements RawData {
	
	@GraphProperty("serviceId")
	@CsvFieldOrdering(position=0)
	private String service_id;
	
	@GraphProperty("date")
	@CsvFieldOrdering(position=1)
	private String date;
	
	@GraphProperty("exceptionType")
	@CsvFieldOrdering(position=2)
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
