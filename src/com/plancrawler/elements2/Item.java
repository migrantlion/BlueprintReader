package com.plancrawler.elements2;

import java.io.Serializable;

import com.plancrawler.elements.ItemSettings;
import com.plancrawler.utilities.MyPoint;

public class Item implements Serializable{

	private static final long serialVersionUID = 1L;

	private ItemSettings settings;
	private MyPoint location;
	private boolean isDisplay;
	
	public Item(ItemSettings settings, MyPoint loc) {
		this.settings = settings;
		this.location = loc;
		this.isDisplay = true;
	}

	public ItemSettings getSettings() {
		return settings;
	}

	public void setSettings(ItemSettings settings) {
		this.settings = settings;
	}

	public MyPoint getLocation() {
		return location;
	}

	public void setLocation(MyPoint location) {
		this.location = location;
	}

	public boolean isDisplay() {
		return isDisplay;
	}

	public void setDisplay(boolean isDisplay) {
		this.isDisplay = isDisplay;
	}
	
	public int getDiameter() {
		// currently have all items with a 50pt diameter
		return 50;
	}
}
