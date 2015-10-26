package com.raeffray.graph;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

@JsonInclude(Include.NON_NULL)
public class RelationshipJsonDescriber {

	public RelationshipJsonDescriber(String type) {
		super();
		this.type = type;
	}

	private String type;

	private String to;
	
	public void setTo(String to) {
		this.to = to;
	}

	@JsonProperty("data")
	private Map<String, Object> attributes;

	public String getType() {
		return type;
	}

	public String getTo() {
		return to;
	}
	
	public Object getAttribute(String key){
		return attributes.get(key);
	}

	public void addAttribute(String key, Object value) {
		if (attributes == null) {
			attributes = new HashMap<String, Object>();
		}
		attributes.put(key, value);
	}

	public String parseJson() throws Exception {
		ObjectWriter ow = new ObjectMapper().writer()
				.withDefaultPrettyPrinter();
		return ow.writeValueAsString(this);
	}

}
