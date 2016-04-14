package com.plancrawler.elements;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;

import com.plancrawler.utilities.MyPoint;

public class Item implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private ItemSettings settings;
	private ArrayList<Mark> marks;

	public Item(String name) {
		this.name = name;
		this.marks = new ArrayList<Mark>();
		this.settings = new ItemSettings(name);
	}

	public Item(ItemSettings settings) {
		this.name = settings.getName();
		this.settings = settings;
		this.marks = new ArrayList<Mark>();
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

	public void addMark(double x, double y, int pageNum) {
		marks.add(new Mark(x, y, pageNum, settings.getColor()));
	}

	public void addMark(MyPoint pos, int pageNum) {
		marks.add(new Mark(pos, pageNum, settings.getColor()));
	}

	public String getName() {
		return name;
	}

	public String getCategory() {
		return settings.getCategory();
	}
	
	public String getDescription() {
		return settings.getDescription();
	}

	public Color getColor() {
		return settings.getColor();
	}

	private void updateMarkSettings() {
		for (Mark m : marks)
			m.setColor(settings.getColor());
	}
	
	public void setColorSettings(Color color) {
		this.settings.setColor(color);
		updateMarkSettings();
	}

	public ItemSettings getSettings() {
		return settings;
	}

	public void setSettings(ItemSettings settings) {
		this.settings = settings;
		updateMarkSettings();
	}
	
}
