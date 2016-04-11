package com.plancrawler.gui;

import java.awt.Graphics;

import com.plancrawler.utilities.MyPoint;

public interface Paintable {
	
	public void paint(Graphics g, double scale, MyPoint origin);
}
