package com.plancrawler.gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;

public class MainWindow {

	private final int WIDTH = 1820;
	private final int HEIGHT = 1080;
	
	private JFrame mainWindow;
	private Screen display;
	
	public MainWindow() {
		mainWindow = new JFrame("PlanCrawler");
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainWindow.setSize(WIDTH, HEIGHT);
		mainWindow.setVisible(true);
	}
	
	public void initialize() {
		int centerWidth = (int) (0.75*mainWindow.getWidth());
		int centerHeight = (int) (0.75*mainWindow.getHeight());
		display = new Screen(centerWidth, centerHeight);
		
		mainWindow.add(display, BorderLayout.CENTER);
	}
	
}
