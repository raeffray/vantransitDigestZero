package com.gtransit.graph.inserter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserters;

import com.gtransit.graph.RelationshipDescriber;
import com.gtransit.raw.data.RawData;
import com.gtransit.reflection.ReflectionData;

public class BatchInsertionGraphDB {

	BatchInserter inserter = null;

	private static BatchInsertionGraphDB instance;

	static Logger logger = Logger.getLogger(BatchInsertionGraphDB.class);

	private BatchInsertionGraphDB() {
		inserter = BatchInserters.inserter(new File("target/graph-db")
				.getAbsolutePath());
	}

	public static BatchInsertionGraphDB getInstance() {
		if (instance == null) {
			instance = new BatchInsertionGraphDB();
			return instance;
		}
		return instance;
	}

	public void shutdown() {
		inserter.shutdown();
	}

	public Map<String, Long> createNodes(String[] labels,
			List<RawData> rawDataList, Class clazz,
			RelationshipDescriber relationship) throws Exception {

		List<Label> labelList = new ArrayList<Label>();
		Map<String, Long> createdNodes = new HashMap<String, Long>();
		String identifier = null;

		for (int i = 0; i < labels.length; i++) {
			Label label = DynamicLabel.label(labels[i]);
			labelList.add(label);

		}
		Label[] arrayLabels = new Label[labelList.size()];
		for (RawData rawData : rawDataList) {
			if (identifier != null) {
				identifier = rawData.indentifier();
			}
			Map<String, Object> properties = ReflectionData.getInstance()
					.getFieldValue(rawData, clazz);
			long nodeId = inserter.createNode(properties,
					labelList.toArray(arrayLabels));
			createdNodes.put(rawData.indentifier(), nodeId);

			// creating relationships
			if (relationship != null) {
				RelationshipType type = DynamicRelationshipType
						.withName(relationship.getType());
				inserter.createRelationship(relationship.getFrom(), nodeId,
						type, relationship.getAttributes());
			}
		}

		return createdNodes;
	}

	public void createRelationships(RelationshipDescriber relationship) {
		RelationshipType type = DynamicRelationshipType.withName(relationship
				.getType());
		inserter.createRelationship(relationship.getFrom(),
				relationship.getTo(), type, relationship.getAttributes());
	}

	public Map<String, Long> createNodeStructure(List<RawData> childNodes,
			String[] childLabels, RelationshipDescriber relationship,
			Class clazz) throws Exception {

		Map<String, Long> createdNodes = createNodes(childLabels, childNodes,
				clazz, relationship);

		return createdNodes;
	}

}
