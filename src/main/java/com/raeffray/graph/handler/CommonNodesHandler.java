package com.raeffray.graph.handler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.raeffray.csv.CSVReader;
import com.raeffray.raw.data.RawData;
import com.raeffray.raw.data.Shapes;
import com.raeffray.raw.data.StopTimes;
import com.raeffray.raw.data.Stops;
import com.raeffray.reflection.ReflectionData;
import com.raeffray.rest.cient.RestClient;

public class CommonNodesHandler {

	static Logger logger = Logger.getLogger(CommonNodesHandler.class);

	public void createShapeNode() {
		CSVReader reader = new CSVReader();
		try {
			List<RawData> shapeList = ReflectionData.getInstance().buildList(
					Shapes.class, reader.readCSVForData(Shapes.class));

			String[] labels = { "SHAPE", "V0" };

			RestClient.getInstance().createNodes(labels, shapeList);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void createStopNode() {
		CSVReader reader = new CSVReader();
		try {
			logger.info("Creating the list");
			List<Stops> stopList = ReflectionData.getInstance().buildList(
					Stops.class, reader.readCSVForData(Stops.class));

			List<StopTimes> stopTimesList = ReflectionData.getInstance()
					.buildList(StopTimes.class,
							reader.readCSVForData(StopTimes.class));

			logger.info("list created");
			String[] labels = { "STOP", "V0" };

			for (Stops stop : stopList) {
				List<StopTimes> foundStopTimes = findStopTimesByStopId(
						stopTimesList, stop.getStop_id());
				for (StopTimes stopTimes : foundStopTimes) {
					
					
				}

			}

			// RestClient.getInstance().createNodes(labels, stopList);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private List<StopTimes> findStopTimesByStopId(List<StopTimes> allstopTimes,
			String stopId) {
		List<StopTimes> foundStopTimes = new ArrayList<StopTimes>();
		for (Iterator<StopTimes> iterator = allstopTimes.iterator(); iterator
				.hasNext();) {
			StopTimes stopTime = iterator.next();
			if (stopTime.getStop_id().equals(stopTime)) {
				foundStopTimes.add(stopTime);
				iterator.remove();
			}
		}
		return foundStopTimes;
	}

}
