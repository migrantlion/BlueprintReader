package com.plancrawler.elements;

import java.awt.Color;

import com.plancrawler.utilities.MyPoint;
import com.plancrawler.warehouse.CrateMark;

public class StorageItem extends Item {

	private static final long serialVersionUID = 1L;
	private Color crateColor;

	public StorageItem(Settings settings, Color crateColor) {
		super(settings);
		this.crateColor = crateColor;
	}

	public StorageItem(String name, Color crateColor) {
		super(name);
		this.crateColor = crateColor;
	}

	public void setMarkQuantity(int quantity) {
		for (Mark m : marks) {
			CrateMark cm = (CrateMark) m;
			cm.setQuantity(quantity);
		}
	}

	public void setMarkDisplay(boolean state) {
		for (Mark m : marks) {
			CrateMark cm = (CrateMark) m;
			cm.setDisplayMe(state);
		}
	}

	public void addMark(MyPoint pos, int pageNum) {
		marks.add(new CrateMark(pos, pageNum, settings.getColor(), crateColor));
	}

}
