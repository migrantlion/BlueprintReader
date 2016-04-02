package com.plancrawler.measurements;

public class MeasureFactory {

	public MeasureFactory() {
		
	}
	
	public Measure getMeasurement(String name) {
		Measure measure;
		
		if (name.equals("Linear"))
			measure = new LinearMeasure();
		else 
		if (name.equals("Quantity"))
			measure = new QuantMeasure();
		else
			measure = null;
		
		return measure;
	}
}
