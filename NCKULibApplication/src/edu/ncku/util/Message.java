package edu.ncku.util;

import java.io.Serializable;

public class Message implements Serializable {	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9097480473074639009L;
	private String title;
	private String unit;
	private String date;
	private String contents;

	/**
	 * Constructor
	 * 
	 * @param title
	 * @param unit
	 * @param date
	 * @param contents
	 */
	public Message(String title, String unit, String date, String contents) {
		super();
		this.title = title;
		this.unit = unit;
		this.date = date;
		this.contents = contents;
	}

	public String getTitle() {
		return title;
	}

	public String getUnit() {
		return unit;
	}

	public String getDate() {
		return date;
	}

	public String getContents() {
		return contents;
	}

}
