package com.gtransit.raw.data;

import com.gtransit.graph.annotations.GraphProperty;

public class Service implements RawData {

	public Service(String startDate, String endDate, String dayOfWeek,
			String service_id, int id) {
		super();
		this.startDate = startDate;
		this.endDate = endDate;
		this.dayOfWeek = dayOfWeek;
		this.id = id;
		this.service_id = service_id;
	}

	private int id;

	@GraphProperty(value = "starDate")
	private String startDate;

	@GraphProperty(value = "endDate")
	private String endDate;

	@GraphProperty(value = "dayOfWeek")
	private String dayOfWeek;

	@GraphProperty(value = "serviceId")
	private String service_id;

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(String dayOfweek) {
		this.dayOfWeek = dayOfweek;
	}

	public String indentifier() {
		return String.valueOf(this.id);
	}

	public String getService_id() {
		return service_id;
	}

	public void setService_id(String service_id) {
		this.service_id = service_id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
