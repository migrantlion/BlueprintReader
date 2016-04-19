package com.plancrawler.measure;

public enum MeasUnit {
	FEET("ft"), COUNT("cnt"), SQFT("sqft"), INCH("in");
	
	private String unit;
	
	MeasUnit(String unit){
		this.unit = unit;
	}
	
	public String getUnit() {
		return unit;
	}
}
