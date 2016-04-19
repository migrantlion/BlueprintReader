package com.plancrawler.measure;

import com.plancrawler.guiComponents.CalibrationDialog;
import com.plancrawler.utilities.MyPoint;

public class Measure {

	private double[] scaleFactor;

	public Measure(int numPages) {
		this.scaleFactor = new double[numPages];
		setScaleFactorToUnity();
	}

	public void calibrate(MyPoint p1, MyPoint p2, int page) {
		updateScale(page, CalibrationDialog.calibrate(null, MyPoint.dist(p1, p2)));
	}

	private void updateScale(int page, double scale) {
		scaleFactor[page] = scale;
	}

	private void setScaleFactorToUnity() {
		for (int i = 0; i < scaleFactor.length; i++)
			updateScale(i, 1.0d);
	}

	public String measure(MyPoint p1, MyPoint p2, int page) {
		return toFeetInches(MyPoint.dist(p1, p2) * scaleFactor[page]);
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
