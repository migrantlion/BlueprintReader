package com.plancrawler.elements;

import java.io.Serializable;

public class ItemSettings implements Serializable{

	private static final long serialVersionUID = 1L;
	private String name;
	private String category;
	private String description;
	private ColorSettings colorSetting = ColorSettings.getRandColorSettings();
	
	public ItemSettings(String name) {
		this.name = name;
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
