package com.plancrawler.measurements;

public abstract class Measure {

	private double value;
	private String name;
	
	public Measure() {
		this.value = 0;
		this.name = null;
	}
	
	public void setValue(double value) {
		this.value = value;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public double getValue() {
		return value;
	}
}
