package com.plancrawler.warehouse;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import com.plancrawler.elements.Item;
import com.plancrawler.elements.Settings;
import com.plancrawler.gui.*;
import com.plancrawler.utilities.MyPoint;

public class DisplayCrate extends Crate implements Paintable {

	private static final long serialVersionUID = 1L;
	private MyPoint location;
	private int pageNum;

	public DisplayCrate(Settings settings, MyPoint location, int pageNum) {
		super(settings);
		this.location = location;
		this.pageNum = pageNum;
	}

	public DisplayCrate(Crate crate, MyPoint location, int pageNum) {
		super(crate);
		this.location = location;
		this.pageNum = pageNum;
	}

	public ArrayList<Paintable> unwrapPaintable(int page){
		ArrayList<Paintable> paintList = new ArrayList<Paintable>();
		
		for (Item item : getLooseItems()) {
			paintList.addAll(item.getMarks(page));
		}
		
		for (Crate c : getOtherCrates()) {
			if (c.getClass().equals(DisplayCrate.class)) {
				DisplayCrate dc = (DisplayCrate) c;
				if (dc.onPage(page))
				paintList.add(dc);
			}
		}
		return paintList;
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

	@Override
	public void paint(Graphics g, double scale, MyPoint origin) {
		Graphics2D g2 = (Graphics2D) g;

		MyPoint loc = getLocation();

		loc.scale(scale);
		loc.translate(origin);

		g2.setColor(getColor());
		g2.fillRect((int) loc.getX(), (int) loc.getY(), (int) Math.max(50 * scale, 20.),
				(int) Math.max(50 * scale, 20.));

		// add outline color
		g2.setColor(getColor());
		g2.drawRect((int) loc.getX(), (int) loc.getY(), (int) Math.max(50 * scale, 20.),
				(int) Math.max(50 * scale, 20.));

	}

	public boolean atLocation(MyPoint loc, int pageNum2) {
		return ((MyPoint.dist(loc, location) < 50.0) && (pageNum2 == pageNum));
	}

	public boolean onPage(int page) {
		return (page == pageNum);
	}

}
