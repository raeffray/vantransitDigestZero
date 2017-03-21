package com.gtransit.csv;

import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;

import com.gtransit.commons.Configuration;
import com.gtransit.raw.data.RawData;
import com.gtransit.reflection.ReflectionData;

public class CSVReader {

	private static final int COLL_CHECKPOINT = 500000;
	static Logger logger = Logger.getLogger(CSVReader.class);
	
	public Collection<Map<String, String>> readCSVForData(
			Class<? extends RawData> clazz) throws Exception{

		Iterable<CSVRecord> records;

		Set<Map<String, String>> list = new HashSet<Map<String, String>>();

		String csvPath = Configuration.getConfigurationForClass(clazz)
				.getString("csv.path");

		InputStream stream = getClass().getResourceAsStream(csvPath);

		Reader in;

		if (stream != null) {
			in = new InputStreamReader(stream);
		} else {
			in = new FileReader(csvPath);
		}

		String[] fieldsNames = ReflectionData.getInstance().extractFieldsNames(
				clazz);

		records = CSVFormat.EXCEL.withHeader(fieldsNames)
				.withSkipHeaderRecord(true).withIgnoreSurroundingSpaces(true)
				.parse(in);

		for (CSVRecord record : records) {
			Map<String, String> fields = new HashMap<String, String>();
			int fieldsNamesLength = fieldsNames.length;
			for (int i = 0; i < fieldsNamesLength; i++) {
				String field = fieldsNames[i];
				String value = record.get(field).trim();
				fields.put(field, value);
			}
			if(logger.isDebugEnabled() && list.size() > 0 && list.size() % COLL_CHECKPOINT == 0){
				logger.info("reached " + COLL_CHECKPOINT);
			}
			list.add(fields);
		}

		return list;

	}
}
