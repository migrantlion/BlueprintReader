package com.plancrawler.elements;

import java.io.Serializable;
import java.util.ArrayList;

import com.plancrawler.utilities.MyPoint;

public class Item implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private String category;
	private ArrayList<Mark> marks;
	private ColorSettings colorSettings;

	public Item(String name) {
		this.name = name;
		this.marks = new ArrayList<Mark>();
		colorSettings = ColorSettings.getDefaultSettings();
	}

	public ArrayList<Mark> getMarks() {
		return marks;
	}

	public ArrayList<Mark> getMarks(int pageNum) {
		ArrayList<Mark> pageMarks = new ArrayList<Mark>();

		for (Mark m : marks) {
			if (m.getPageNum() == pageNum)
				pageMarks.add(m);
		}

		return pageMarks;
	}
	
	public int count() {
		return marks.size();
	}
	
	public void delMarkAt(MyPoint loc, int pageNum) {
		Mark theMark = getMarkAt(loc, pageNum);
		
		if (theMark != null)
			marks.remove(theMark);
	}
	
	public Mark getMarkAt(MyPoint loc, int pageNum) {	
		Mark theMark = null;
		for (Mark m : marks) {
			//System.out.println("distance: "+m.getLocation().distTo(loc));
			if ((m.getLocation().distTo(loc) <= 50) && (m.getPageNum() == pageNum))
				theMark = m;
		}
		return theMark;
	}

	public void setCategory(String cType) {
		this.category = cType;
	}

	public void addMark(double x, double y, int pageNum) {
		marks.add(new Mark(x, y, pageNum, colorSettings));
	}

	public void addMark(MyPoint pos, int pageNum) {
		marks.add(new Mark(pos, pageNum, colorSettings));
	}

	public String getName() {
		return name;
	}

	public String getCategory() {
		return category;
	}

	public ColorSettings getColorSettings() {
		return colorSettings;
	}

	public void setColorSettings(ColorSettings colorSettings) {
		this.colorSettings = colorSettings;
	}

}
