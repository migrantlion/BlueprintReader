package com.plancrawler.elements;

import java.awt.Color;
import java.io.Serializable;

public class ItemSettings implements Serializable{

	private static final long serialVersionUID = 1L;
	private String name;
	private String category = "none";
	private String description;
	private ColorSettings colorSetting = ColorSettings.getRandColorSettings();
	
	public ItemSettings(String name) {
		this.name = name;
	}
	
	public ItemSettings(String name, String desc, Color color) {
		this.name = name;
		this.description = desc;
		this.colorSetting = new ColorSettings(color);
	}
	
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public ColorSettings getColorSetting() {
		return colorSetting;
	}
	public void setColorSetting(ColorSettings colorSetting) {
		this.colorSetting = colorSetting;
	}
	public String getName() {
		return name;
	}
	
}
