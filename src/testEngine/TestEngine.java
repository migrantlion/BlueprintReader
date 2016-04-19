package testEngine;

import com.plancrawler.gui.GUI;

public class TestEngine {

	public static void main(String[] args) {

		GUI gui = new GUI();
		gui.init();

		boolean running = true;
		while (running) {
			gui.updateComponents();
		}		
	}

}
