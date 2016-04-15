package com.plancrawler.elements;

import java.awt.Color;
import java.io.Serializable;

public class Settings implements Serializable, Comparable<Settings>{

	private static final long serialVersionUID = 1L;
	private String name;
	private String category = "none";
	private String description;
	private Color color = new Color((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255));
	
	public Settings(String name) {
		this.name = name;
	}
	
	public Settings(String name, String desc, Color color) {
		this.name = name;
		this.description = desc;
		this.color = color;
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
	public Color getColor() {
		return color;
	}
	public Color getInvColor() {
		return new Color(255-color.getRed(), 255-color.getGreen(), 255-color.getBlue());
	}
	public void setColor(Color color) {
		this.color = color;
	}
	public String getName() {
		return name;
	}

	@Override
	public int compareTo(Settings other) {
		if (this.category.compareTo(other.category) == 0)
			return this.name.compareTo(other.name);
		else
			return this.category.compareTo(other.category);
	}
	
}
