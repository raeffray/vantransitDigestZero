package com.gtransit.graph;

import java.util.Map;

public class RelationshipDescriber {

	private long from;

	private long to;

	private String type;

	private Map<String, Object> attributes;

	public RelationshipDescriber(long from, long to, String type,
			Map<String, Object> attributes) {
		super();
		this.from = from;
		this.to = to;
		this.type = type;
		this.attributes = attributes;
	}

	public long getFrom() {
		return from;
	}

	public void setFrom(long from) {
		this.from = from;
	}

	public long getTo() {
		return to;
	}

	public void setTo(long to) {
		this.to = to;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

}
