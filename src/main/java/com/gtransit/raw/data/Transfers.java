package com.gtransit.raw.data;

import com.gtransit.csv.annotations.CsvFieldOrdering;
import com.gtransit.graph.annotations.GraphProperty;

public class Transfers implements RawData {

	@GraphProperty("from")
	@CsvFieldOrdering(position=0)
	private String from_stop_id;

	@GraphProperty("to")
	@CsvFieldOrdering(position=1)
	private String to_stop_id;

	@GraphProperty("type")
	@CsvFieldOrdering(position=2)
	private String transfer_type;

	@GraphProperty("minTransferTime")
	@CsvFieldOrdering(position=3)
	private String min_transfer_time;

	public String getFrom_stop_id() {
		return from_stop_id;
	}

	public void setFrom_stop_id(String from_stop_id) {
		this.from_stop_id = from_stop_id;
	}

	public String getTo_stop_id() {
		return to_stop_id;
	}

	public void setTo_stop_id(String to_stop_id) {
		this.to_stop_id = to_stop_id;
	}

	public String getTransfer_type() {
		return transfer_type;
	}

	public void setTransfer_type(String transfer_type) {
		this.transfer_type = transfer_type;
	}

	public String getMin_transfer_time() {
		return min_transfer_time;
	}

	public void setMin_transfer_time(String min_transfer_time) {
		this.min_transfer_time = min_transfer_time;
	}

	public String indentifier() {
		return null;
	}

}
