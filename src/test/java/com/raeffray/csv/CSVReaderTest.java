package com.raeffray.csv;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.gtransit.csv.CSVReader;
import com.gtransit.raw.data.Agency;
import com.gtransit.raw.data.Calendar;
import com.gtransit.raw.data.CalendarDates;
import com.gtransit.raw.data.Routes;
import com.gtransit.raw.data.Shapes;
import com.gtransit.raw.data.StopTimes;
import com.gtransit.raw.data.Stops;
import com.gtransit.raw.data.Transfers;
import com.gtransit.raw.data.Trips;

public class CSVReaderTest {
	
	private CSVReader csvReader;
	
	@Before
	public void prepare(){
		csvReader = new CSVReader();
	}
	
	@Test
	public void testRoutes() throws Exception{
		Collection<Map<String, String>> data = csvReader.readCSVForData(Routes.class);
		assertThat(data, notNullValue());
		assertThat(data.size(),greaterThan(0));
		assertThat(data.size(),greaterThan(0));
		assertThat(data.iterator().next().get("route_id"),is(equalTo("AAAA")));
		assertThat(data.iterator().next().get("route_text_color"),is(equalTo("IIII")));

	}
	
	@Test
	public void testTrips() throws Exception{
		Collection<Map<String, String>> data = csvReader.readCSVForData(Trips.class);
		assertThat(data, notNullValue());
		assertThat(data.size(),greaterThan(0));
		assertThat(data.size(),greaterThan(0));
		assertThat(data.iterator().next().get("route_id"),is(equalTo("AAAA")));
		assertThat(data.iterator().next().get("shape_id"),is(equalTo("HHHH")));
	}

	@Test
	public void testTimes() throws Exception{
		Collection<Map<String, String>> data = csvReader.readCSVForData(StopTimes.class);
		assertThat(data, notNullValue());
		assertThat(data.size(),greaterThan(0));
		assertThat(data.size(),greaterThan(0));
		assertThat(data.iterator().next().get("trip_id"),is(equalTo("AAAA")));
		assertThat(data.iterator().next().get("shape_dist_traveled"),is(equalTo("III1")));
	}
	
	@Test
	public void testShapes() throws Exception{
		Collection<Map<String, String>> data = csvReader.readCSVForData(Shapes.class);
		assertThat(data, notNullValue());
		assertThat(data.size(),greaterThan(0));
		assertThat(data.size(),greaterThan(0));
		assertThat(data.iterator().next().get("shape_id"),is(equalTo("AAAA")));
		assertThat(data.iterator().next().get("shape_dist_traveled"),is(equalTo("EEEE")));
	}
	
	@Test
	public void testAgency() throws Exception{
		Collection<Map<String, String>> data = csvReader.readCSVForData(Agency.class);
		assertThat(data, notNullValue());
		assertThat(data.size(),greaterThan(0));
		assertThat(data.size(),greaterThan(0));
		assertThat(data.iterator().next().get("agency_id"),is(equalTo("AAAA")));
		assertThat(data.iterator().next().get("agency_lang"),is(equalTo("EEEE")));
	}
	
	@Test
	public void testStops() throws Exception{
		Collection<Map<String, String>> data = csvReader.readCSVForData(Stops.class);
		assertThat(data, notNullValue());
		assertThat(data.size(),greaterThan(0));
		assertThat(data.size(),greaterThan(0));
		assertThat(data.iterator().next().get("stop_id"),is(equalTo("AAAA")));
		assertThat(data.iterator().next().get("parent_station"),is(equalTo("JJJJ")));
	}
	@Test
	public void testCalendar() throws Exception{
		Collection<Map<String, String>> data = csvReader.readCSVForData(Calendar.class);
		assertThat(data, notNullValue());
		assertThat(data.size(),greaterThan(0));
		assertThat(data.size(),greaterThan(0));
		assertThat(data.iterator().next().get("service_id"),is(equalTo("AAAA")));
		assertThat(data.iterator().next().get("end_date"),is(equalTo("JJJJ")));
	}
	@Test
	public void testCalendarDates() throws Exception{
		Collection<Map<String, String>> data = csvReader.readCSVForData(CalendarDates.class);
		assertThat(data, notNullValue());
		assertThat(data.size(),greaterThan(0));
		assertThat(data.size(),greaterThan(0));
		assertThat(data.iterator().next().get("service_id"),is(equalTo("AAAA")));
		assertThat(data.iterator().next().get("exception_type"),is(equalTo("CCCC")));
	}
	@Test
	public void testCalendarTransfers() throws Exception{
		Collection<Map<String, String>> data = csvReader.readCSVForData(Transfers.class);
		assertThat(data, notNullValue());
		assertThat(data.size(),greaterThan(0));
		assertThat(data.size(),greaterThan(0));
		assertThat(data.iterator().next().get("from_stop_id"),is(equalTo("AAAA")));
		assertThat(data.iterator().next().get("min_transfer_time"),is(equalTo("DDDD")));
	}
}
