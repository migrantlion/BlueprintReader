package com.plancrawler.elements;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.Serializable;

import com.plancrawler.gui.Paintable;
import com.plancrawler.utilities.MyPoint;

public class Mark implements Serializable, Paintable {

	private static final long serialVersionUID = 1L;
	private MyPoint location;
	private double radius;
	private int pageNum;
	private ColorSettings colorSettings;

	public Mark(int pageNum, ColorSettings colorSettings) {
		this.pageNum = pageNum;
		this.colorSettings = colorSettings;
		this.radius = 50.0;
	}

	public Mark(double x, double y, int pageNum, ColorSettings colorSettings) {
		this(pageNum, colorSettings);
		this.location = new MyPoint(x, y);
	}

	public Mark(MyPoint loc, int pageNum, ColorSettings colorSettings) {
		this(pageNum, colorSettings);
		this.location = loc;
	}

	public MyPoint getLocation() {
		return new MyPoint(location);
	}

	public void setLocation(MyPoint location) {
		this.location = location;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public ColorSettings getColorSettings() {
		return colorSettings;
	}

	public void setColorSettings(ColorSettings colorSettings) {
		this.colorSettings = colorSettings;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public int getDiameter() {
		return (int) (radius * 2);
	}

	@Override
	public void paint(Graphics g, double scale, MyPoint origin) {
		Graphics2D g2 = (Graphics2D) g;
		
		MyPoint loc = getLocation();

		loc.scale(scale);
		loc.translate(origin);

		g2.setColor(getColorSettings().getFillColor());
		g2.fillOval((int) loc.getX(), (int) loc.getY(), (int) Math.min(getDiameter() * scale, 20.),
				(int) Math.min(getDiameter() * scale, 20.));

	}
}
