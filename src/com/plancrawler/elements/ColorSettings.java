package com.plancrawler.elements;

import java.awt.Color;

public class ColorSettings {
	private Color lineColor, fillColor;
	private float opacity;
	
	public ColorSettings(Color lineColor, Color fillColor, float opacity) {
		this.lineColor = lineColor;
		this.fillColor = fillColor;
		this.opacity = opacity;
	}

	public Color getLineColor() {
		return lineColor;
	}

	public void setLineColor(Color lineColor) {
		this.lineColor = lineColor;
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

	public float getOpacity() {
		return opacity;
	}

	public void setOpacity(float opacity) {
		this.opacity = opacity;
	}

	public static ColorSettings getDefaultSettings() {		
		return new ColorSettings(Color.blue, Color.blue, 1.0f);
	}

}
