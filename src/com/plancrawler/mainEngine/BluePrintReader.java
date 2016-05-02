package com.plancrawler.mainEngine;

import com.plancrawler.gui.GUI;

public class BluePrintReader {

	public static void main(String[] args) {

		GUI gui = new GUI();
		gui.init();

		boolean running = true;
		while (running) {
			gui.updateComponents();
		}		
	}

}
