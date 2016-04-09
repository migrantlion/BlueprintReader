package com.plancrawler.elements;

import java.awt.Color;
import java.io.Serializable;

public class ColorSettings implements Serializable{

	private static final long serialVersionUID = 1L;
	private Color fillColor;
	
	public ColorSettings(Color fillColor) {
		this.fillColor = fillColor;
	}

	public Color getFillColor() {
		return fillColor;
	}

	public void setFillColor(Color fillColor) {
		this.fillColor = fillColor;
	}
	
	public Color getInvFillColor() {
		Color invColor = new Color(255-fillColor.getRed(), 255-fillColor.getGreen(), 255-fillColor.getBlue());
		return invColor;
	}

	public static ColorSettings getDefaultSettings() {		
		return new ColorSettings(Color.blue);
	}
	
	public static ColorSettings getRandColorSettings() {
		Color color = new Color((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255));
		return new ColorSettings(color);
	}

}
