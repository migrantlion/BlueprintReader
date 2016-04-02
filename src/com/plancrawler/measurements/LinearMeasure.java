package com.plancrawler.measurements;

import images.MyPoint;

public class LinearMeasure extends Measure{

	public LinearMeasure() {
		super();
		setName("Linear");
	}
	
	public void setValue(MyPoint pt1, MyPoint pt2) {
		double distance = MyPoint.dist(pt1, pt2);
		super.setValue(distance);
	}

}
