package com.gtransit.commons;

import java.util.ArrayList;
import java.util.List;

import com.gtransit.raw.data.Calendar;
import com.gtransit.raw.data.Service;

public class BeanPopulator {

	public static List<Service> createServicesForDayInWeek(
			List<Calendar> calendarList) {

		List<Service> services = new ArrayList<Service>();

		int count = 1;

		for (Calendar calendar : calendarList) {
			boolean hasDayOfWeek = false;
			if (calendar.getMonday().equals("1")) {
				hasDayOfWeek = true;
				services.add(new Service(calendar.getStart_date(), calendar
						.getEnd_date(), "monday", calendar.getService_id(),
						count++));
			}
			if (calendar.getTuesday().equals("1")) {
				hasDayOfWeek = true;
				services.add(new Service(calendar.getStart_date(), calendar
						.getEnd_date(), "tuesday", calendar.getService_id(),
						count++));
			}
			if (calendar.getWednesday().equals("1")) {
				hasDayOfWeek = true;
				services.add(new Service(calendar.getStart_date(), calendar
						.getEnd_date(), "wednesday", calendar.getService_id(),
						count++));
			}
			if (calendar.getThursday().equals("1")) {
				hasDayOfWeek = true;
				services.add(new Service(calendar.getStart_date(), calendar
						.getEnd_date(), "thursday", calendar.getService_id(),
						count++));
			}
			if (calendar.getFriday().equals("1")) {
				hasDayOfWeek = true;
				services.add(new Service(calendar.getStart_date(), calendar
						.getEnd_date(), "friday", calendar.getService_id(),
						count++));
			}
			if (calendar.getSaturday().equals("1")) {
				hasDayOfWeek = true;
				services.add(new Service(calendar.getStart_date(), calendar
						.getEnd_date(), "saturday", calendar.getService_id(),
						count++));
			}
			if (calendar.getSunday().equals("1")) {
				hasDayOfWeek = true;
				services.add(new Service(calendar.getStart_date(), calendar
						.getEnd_date(), "sunday", calendar.getService_id(),
						count++));
			}
			if (!hasDayOfWeek) {
				services.add(new Service(calendar.getStart_date(), calendar
						.getEnd_date(), "", calendar.getService_id(), count++));
			}
		}

		return services;
	}

}
