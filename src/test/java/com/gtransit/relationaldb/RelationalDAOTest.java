package com.gtransit.relationaldb;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.List;

import org.junit.Test;

import com.gtransit.raw.data.RawData;
import com.gtransit.raw.data.StopTimes;


public class RelationalDAOTest {
	
	@Test
	public void testSaveLoadStopTimes() throws Exception{
		
		RelationalDAO.getInstance().insertData(StopTimes.class);
		
		List<RawData> list = RelationalDAO.getInstance().findStopTimesByTripId("AAAA");
		
		assertThat(list.size(), equalTo(4));
		
	}

}
