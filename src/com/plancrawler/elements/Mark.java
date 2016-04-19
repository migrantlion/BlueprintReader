package com.plancrawler.elements;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.Serializable;

import com.plancrawler.gui.Paintable;
import com.plancrawler.measure.Countable;
import com.plancrawler.utilities.MyPoint;

public class Mark implements Serializable, Paintable, Countable {

	private static final long serialVersionUID = 1L;
	private MyPoint location;
	private int pageNum;
	private Color color, outlineColor;

	public Mark(int pageNum, Color color) {
		this.pageNum = pageNum;
		this.color = color;
		this.outlineColor = color;
	}

	public Mark(MyPoint loc, int pageNum, Color color) {
		this(pageNum, color);
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

	public void setColor(Color color) {
		this.color = color;
	}

	public Color getOutlineColor() {
		return outlineColor;
	}

	public void setOutlineColor(Color outlineColor) {
		this.outlineColor = outlineColor;
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
		
		// add outline color
		g2.setColor(getOutlineColor());
		g2.drawOval((int) loc.getX(), 
				(int) loc.getY(), 
				(int) Math.max(50 * scale, 20.),
				(int) Math.max(50 * scale, 20.)
				);

	}

	@Override
	public float getQuantity() {
		return 1;
	}

}
