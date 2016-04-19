package com.plancrawler.warehouse;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import com.plancrawler.elements.Mark;
import com.plancrawler.utilities.MyPoint;

public class CrateMark extends Mark {
	private static final long serialVersionUID = 1L;

	private int quantity = 0;
	private boolean displayMe = true;

	public CrateMark(MyPoint point, int pageNum, Color color) {
		super(point, pageNum, color);
	}

	public CrateMark(MyPoint point, int pageNum, Color color, Color outline) {
		super(point, pageNum, color);
		setOutlineColor(outline);
	}

	public void setQuantity(int value) {
		this.quantity = value;
	}

	public float getQuantity() {
		return quantity;
	}
	
	public void setDisplayMe(boolean state) {
//		this.displayMe = state;
	}
	
	@Override
	public void paint(Graphics g, double scale, MyPoint origin) {
		if (displayMe) {
			Graphics2D g2 = (Graphics2D) g;

			MyPoint loc = getLocation();

			loc.scale(scale);
			loc.translate(origin);
			
			Color color = getColor();
			Color transcolor = new Color(color.getRed()/255f, color.getGreen()/255f, color.getBlue()/255f, 0.5f);
			g2.setColor(transcolor);
			g2.fillRect((int) loc.getX(), (int) loc.getY(), (int) Math.max(50 * scale, 20.),
					(int) Math.max(50 * scale, 20.));

			// add outline color
			g2.setColor(getOutlineColor());
			g2.drawRect((int) loc.getX(), (int) loc.getY(), (int) Math.max(50 * scale, 20.),
					(int) Math.max(50 * scale, 20.));

		}
	}
}
