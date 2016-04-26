package com.plancrawler.measure;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

import com.plancrawler.elements.Mark;
import com.plancrawler.gui.Paintable;
import com.plancrawler.utilities.MyPoint;

public class MeasMark extends Mark implements Paintable {
	private static final long serialVersionUID = 1L;
	private MyPoint start, finish;
	private String measVal;

	public MeasMark(MyPoint start, MyPoint finish, int pageNum, Color color) {
		super(new MyPoint(finish).add(MyPoint.neg(start)), pageNum, color);
		this.start = start;
		this.finish = finish;
	}

	public MeasMark(MyPoint start, MyPoint finish, String val, int pageNum) {
		this(start, finish, pageNum, Color.red);
		this.measVal = val;
	}

	public void setMeasVal(String val) {
		this.measVal = val;
	}
	
	public boolean isVerticalOrientation() {
		MyPoint line = new MyPoint(finish).add(MyPoint.neg(start));
		double vert = Math.abs(MyPoint.dot(new MyPoint(0, 1), line));
		double horiz = Math.abs(MyPoint.dot(new MyPoint(1, 0), line));
		
		// if dot product == 0, then line is perp to choice
		if (vert > horiz)
			return true;
		else
			return false;
	}

	public boolean isAtLocation(MyPoint testPoint) {
		boolean answer;

		if (isVerticalOrientation()) {
			MyPoint[] border = { new MyPoint(start.getX() - 25, start.getY()),
					new MyPoint(start.getX() + 25, start.getY()), new MyPoint(finish.getX() + 25, finish.getY()),
					new MyPoint(finish.getX() - 25, finish.getY()) };

			answer = MyPoint.isInsideRegion(testPoint, border);
		} else {
			MyPoint[] border = { new MyPoint(start.getX(), start.getY() - 25),
					new MyPoint(start.getX(), start.getY() + 25), new MyPoint(finish.getX(), finish.getY() + 25),
					new MyPoint(finish.getX(), finish.getY() - 25) };

			answer = MyPoint.isInsideRegion(testPoint, border);
		}
		return answer;
	}

	@Override
	public void paint(Graphics g, double scale, MyPoint origin) {
		Graphics2D g2 = (Graphics2D) g;
		int xmark = (int) Math.floor(scale * 25);

		MyPoint loc1 = new MyPoint(start);
		loc1.scale(scale);
		loc1.translate(origin);

		MyPoint loc2 = new MyPoint(finish);
		loc2.scale(scale);
		loc2.translate(origin);

		g2.setColor(getColor());
		// draw first X
		g2.drawLine((int) (loc1.getX() - xmark), (int) (loc1.getY() - xmark), (int) (loc1.getX() + xmark),
				(int) (loc1.getY() + xmark));
		g2.drawLine((int) (loc1.getX() - xmark), (int) (loc1.getY() + xmark), (int) (loc1.getX() + xmark),
				(int) (loc1.getY() - xmark));
		// draw second X
		g2.drawLine((int) (loc2.getX() - xmark), (int) (loc2.getY() - xmark), (int) (loc2.getX() + xmark),
				(int) (loc2.getY() + xmark));
		g2.drawLine((int) (loc2.getX() - xmark), (int) (loc2.getY() + xmark), (int) (loc2.getX() + xmark),
				(int) (loc2.getY() - xmark));
		
		// draw connecting line
		g2.drawLine((int)loc1.getX(), (int) loc1.getY(), (int)loc2.getX(), (int)loc2.getY());

		// scale the font size
		Font currentFont = g2.getFont();
		Font newFont = currentFont.deriveFont((float) (currentFont.getSize() * 5.6F * scale));
		g2.setFont(newFont);
		
		// find out how big the text will be
		FontMetrics metrics = g2.getFontMetrics(newFont);
		int hgt = metrics.getHeight();
		int adv = metrics.stringWidth(measVal);
		Dimension textSize = new Dimension(adv+2, hgt+2);
		
		// find the center of the line and adjust back by the text width
		MyPoint centerPt = MyPoint.middle(loc1, loc2);
		centerPt.add(new MyPoint(-textSize.getWidth()/2d,0));
		
		// draw in a background box
		g2.setColor(Color.white);
		g2.fillRect((int) centerPt.getX(), (int) (centerPt.getY() - textSize.getHeight()), (int)textSize.getWidth(), (int)textSize.getHeight());
		
		g2.setColor(getColor());
		g2.drawString(measVal, (int) centerPt.getX(), (int) centerPt.getY());

		g2.setFont(currentFont);
	}
}
