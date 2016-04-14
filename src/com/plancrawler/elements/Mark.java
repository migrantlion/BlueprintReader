package com.plancrawler.elements;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.Serializable;

import com.plancrawler.gui.Paintable;
import com.plancrawler.utilities.MyPoint;

public class Mark implements Serializable, Paintable {

	private static final long serialVersionUID = 1L;
	private MyPoint location;
	private int pageNum;
	private Color color;

	public Mark(int pageNum, Color colorSettings) {
		this.pageNum = pageNum;
		this.color = colorSettings;
	}

	public Mark(double x, double y, int pageNum, Color colorSettings) {
		this(pageNum, colorSettings);
		this.location = new MyPoint(x, y);
	}

	public Mark(MyPoint loc, int pageNum, Color colorSettings) {
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

	public Color getColor() {
		return color;
	}

	public void setColor(Color colorSettings) {
		this.color = colorSettings;
	}

	@Override
	public void paint(Graphics g, double scale, MyPoint origin) {
		Graphics2D g2 = (Graphics2D) g;
		
		MyPoint loc = getLocation();

		loc.scale(scale);
		loc.translate(origin);

		g2.setColor(getColor());
		g2.fillOval(
				(int) loc.getX(), 
				(int) loc.getY(), 
				(int) Math.max(50 * scale, 20.),
				(int) Math.max(50 * scale, 20.)
				);

	}
}
