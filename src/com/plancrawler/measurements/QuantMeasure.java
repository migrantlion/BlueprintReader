package com.plancrawler.measurements;

public class QuantMeasure extends Measure {

	private int count = 0;
	
	public QuantMeasure() {
		super();
		setName("Quantity");
	}
	
	public void addCount() {
		count += 1;
		setValue(count);
	}
	
	public void addCount(int num) {
		count += num;
		setValue(count);
	}
	
	public int getCount() {
		return count;
	}
	
	public void zero() {
		count = 0;
		setValue(count);
	}
}
