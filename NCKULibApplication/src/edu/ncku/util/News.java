package edu.ncku.util;

import java.io.Serializable;

public class News implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8063030020205138201L;
	private String title;
	private String unit;
	private int timeStamp;
	private String contents;

	public News(String title, String unit, int timeStamp, String contents) {
		super();
		this.title = title;
		this.unit = unit;
		this.timeStamp = timeStamp;
		this.contents = contents;
	}

	public String getTitle() {
		return title;
	}

	public String getUnit() {
		return unit;
	}

	public int getTimeStamp() {
		return timeStamp;
	}

	public String getContents() {
		return contents;
	}
	
}
