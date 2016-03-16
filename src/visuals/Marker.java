package visuals;

import java.awt.Color;

public class Marker {

	private double[][] pts, originals;
	private Color color;
	private static int[] xfactor = {0, 1, 1, 0};
	private static int[] yfactor = {0, 0, 1, 1};

	public Marker(int x, int y, int w, int h, Color c) {
		this.pts = new double[4][2];
		
		for (int i = 0; i <4; i++){
			pts[i][0] = x + xfactor[i]*w;
			pts[i][1] = y + yfactor[i]*h;
		}
		this.originals = pts;
		this.color = c;
	}

	public void move(double dx, double dy) {
		for (int i = 0; i < 4; i++){
			pts[i][0] += dx;
			pts[i][1] += dy;
		}
	}

	public void scale(double s) {
		// scales with respect to origin
		for (int i = 0; i < 4; i++){
			pts[i][0] = s*originals[i][0];
			pts[i][1] = s*originals[i][1];
		}
	}

	public int getX() {
		return (int) Math.floor(pts[0][0]);
	}

	public int getY() {
		return (int) Math.floor(pts[0][1]);
	}

	public int getWidth() {
		double minx = 9999;
		double maxx = 0;
		for (int i = 0; i < 4; i++){
			if (minx > pts[i][0])
				minx = pts[i][0];
			if (maxx < pts[i][0])
				maxx = pts[i][0];
		}
		return (int) Math.floor(maxx - minx);
	}

	public int getHeight() {
		double miny = 9999;
		double maxy = 0;
		for (int i = 0; i < 4; i++){
			if (miny > pts[i][1])
				miny = pts[i][1];
			if (maxy < pts[i][1])
				maxy = pts[i][1];
		}
		return (int) Math.floor(maxy - miny);
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

}
