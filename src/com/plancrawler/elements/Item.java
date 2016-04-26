package com.plancrawler.elements;

import java.awt.Color;
import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;

import com.plancrawler.utilities.MyPoint;

public class Item implements Serializable {

	private static final long serialVersionUID = 1L;

	protected Settings settings;
	protected CopyOnWriteArrayList<Mark> marks;

	public Item(String name) {
		this.marks = new CopyOnWriteArrayList<Mark>();
		this.settings = new Settings(name);
	}

	public Item(Settings settings) {
		this.settings = settings;
		this.marks = new CopyOnWriteArrayList<Mark>();
	}

	public CopyOnWriteArrayList<Mark> getMarks() {
		return marks;
	}

	public CopyOnWriteArrayList<Mark> getMarks(int pageNum) {
		CopyOnWriteArrayList<Mark> pageMarks = new CopyOnWriteArrayList<Mark>();

		for (Mark m : marks) {
			if (m.getPageNum() == pageNum)
				pageMarks.add(m);
		}

		return pageMarks;
	}
	
	public int count() {
		int total = 0;
		for (Mark m : marks)
			total += m.getQuantity();
		
		return total;
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

	public void addMark(MyPoint pos, int pageNum) {
		marks.add(new Mark(pos, pageNum, settings.getColor()));
	}
	
	public void insertMark(Mark mark) {
		marks.add(mark);
	}

	public String getName() {
		return settings.getName();
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

	public Settings getSettings() {
		return settings;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
		updateMarkSettings();
	}
	
	public static boolean ofSameThing(Item a, Item b) {
		return (a.settings.compareTo(b.settings) == 0);
	}
}
