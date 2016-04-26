package com.plancrawler.measure;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import com.plancrawler.gui.Paintable;
import com.plancrawler.utilities.MyPoint;

public class LineMark implements Paintable {

	private MyPoint p1, p2;

	public LineMark(MyPoint p1, MyPoint p2) {
		this.p1 = p1;
		this.p2 = p2;
	}

	@Override
	public void paint(Graphics g, double scale, MyPoint origin) {
		Graphics2D g2 = (Graphics2D) g;

		MyPoint loc1 = new MyPoint(p1);
		loc1.scale(scale);
		loc1.translate(origin);

		MyPoint loc2 = new MyPoint(p2);
		loc2.scale(scale);
		loc2.translate(origin);

		g2.setColor(Color.red);
		// draw first X
		g2.drawLine((int) (loc1.getX() - 25), (int) (loc1.getY() - 25), (int) (loc1.getX() + 25),
				(int) (loc1.getY() + 25));
		g2.drawLine((int) (loc1.getX() - 25), (int) (loc1.getY() + 25), (int) (loc1.getX() + 25),
				(int) (loc1.getY() - 25));
		// then draw line to where pt2 is
		g2.drawLine((int) loc1.getX(), (int) loc1.getY(), (int) loc2.getX(), (int) loc2.getY());
	}

}
