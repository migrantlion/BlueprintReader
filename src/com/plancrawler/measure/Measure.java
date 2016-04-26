package com.plancrawler.measure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.plancrawler.guiComponents.CalibrationDialog;
import com.plancrawler.utilities.MyPoint;

public class Measure implements Serializable {

	private static final long serialVersionUID = 1L;

	// mapping of page number to scaleFactor
	private HashMap<Integer, Double> scaleFactor;
	// all the marks that were measured by the user
	private CopyOnWriteArrayList<MeasMark> measMarks;

	public Measure() {
		this.scaleFactor = new HashMap<Integer, Double>();
		this.measMarks = new CopyOnWriteArrayList<MeasMark>();
	}

	public void calibrate(MyPoint p1, MyPoint p2, int page) {
		updateScale(page, CalibrationDialog.calibrate(null, MyPoint.dist(p1, p2), measure(p1,p2,page)));
	}

	private void updateScale(int page, double scale) {
		scaleFactor.put(page, scale);
	}
	
	private double getScaleFactor(int page) {
		if (scaleFactor.containsKey(page))
			return scaleFactor.get(page);
		else
			return 1.0d;
	}
	
	public CopyOnWriteArrayList<MeasMark> getMarks(int page){
		CopyOnWriteArrayList<MeasMark> marks = new CopyOnWriteArrayList<MeasMark>();
		for (MeasMark m : measMarks)
			if (m.isOnPage(page))
				marks.add(m);
		return marks;
	}

	public void addMeasurement(MyPoint p1, MyPoint p2, int page) {
		measMarks.add(new MeasMark(p1, p2, measure(p1,p2,page), page));
	}
	
	public void delMeasurement(MyPoint loc, int page) {
		ArrayList<MeasMark> removeList = new ArrayList<MeasMark>();
		
		for (MeasMark m : measMarks) {
			if (m.isAtLocation(loc) && m.isOnPage(page))
				removeList.add(m);
		}
		
		for (MeasMark m : removeList)
			measMarks.remove(m);
	}
	
	public String measure(MyPoint p1, MyPoint p2, int page) {
		return toFeetInches(MyPoint.dist(p1, p2) * getScaleFactor(page));
	}

	private String toFeetInches(double meas) {
		int feet, inch;

		feet = (int) (Math.floor(meas));
		// look at inches
		meas -= feet;
		meas *= 12;
		inch = (int) (Math.floor(meas));
		// look at the fraction
		meas -= inch;

		int[] frac = simplify(meas);

		String answer = "";
		if (feet > 0)
			answer = Integer.toString(feet) + "' ";
		if (inch > 0)
			answer += Integer.toString(inch);
		if (frac[0] > 0)
			if (inch > 0)
				answer += "-" + frac[0] + "/" + frac[1] + "\"";
			else
				answer += frac[0] + "/" + frac[1] + "\"";
		else
			answer += "\"";

		return answer;
	}

	private int[] simplify(double fraction) {
		int[] frac = new int[2];

		long a = Math.round(fraction * 16);
		long b = 16L;

		long gcd = gcmdenom(a, b);
		frac[0] = (int) (a / gcd);
		frac[1] = (int) (b / gcd);

		return frac;
	}

	private static long gcmdenom(long a, long b) {
		return b == 0 ? a : gcmdenom(b, a % b);
	}
}
