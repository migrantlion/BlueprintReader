package testEngine;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import gui.GUI;
import guiComponents.ChalkBoardPanel;

public class TestEngine {

	public static void main(String[] args) {

//		JFrame frame = new JFrame("testcode");
//		frame.setSize(1400, 900);
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//
//		ChalkBoardPanel chalkBoard = new ChalkBoardPanel(400, 600);
//		frame.add(chalkBoard, BorderLayout.WEST);
//
//		frame.setVisible(true);

		GUI gui = new GUI();
		gui.init();

		boolean running = true;
		while (running) {
			gui.update();
		}
	}

}
