package com.plancrawler.guiComponents;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

public class MeasureRibbon extends JPanel {

	private static final long serialVersionUID = 1L;
	private boolean isMeasuring = false;
	JToggleButton measButt;
	JToggleButton calButt;

	public MeasureRibbon(int width, int height) {
		this.setSize(width, height);
		this.setLayout(new FlowLayout());
		this.add(setupComponents());
		this.addMouseListener(new MeasMouseListener());
	}

	public MeasureRibbon(Dimension dim) {
		this((int)dim.getWidth(),(int)dim.getHeight());
	}

	private Box setupComponents() {
		MeasListener measListener = new MeasListener();
		Box ribbon = Box.createHorizontalBox();
		JLabel measureLabel = new JLabel("Measurement: ");
		calButt = new JToggleButton("[CAL]");
		measButt = new JToggleButton("[MEAS]");
		
		calButt.addActionListener(measListener);
		measButt.addActionListener(measListener);
		
		ribbon.add(measureLabel);
		ribbon.add(calButt);
		ribbon.add(measButt);
		
		return ribbon;
	}
	
	private synchronized void calibrate() {
		System.out.println("Calibrating");
		isMeasuring = true;
	}

	private synchronized void measure() {
		System.out.println("Measuring");
	}

	public boolean isMeasuring() {
		return isMeasuring;
	}

	private class MeasListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == calButt) {
				if (calButt.isSelected()) {
					calButt.setForeground(Color.red);
					calibrate();
				} else {
					isMeasuring = false;
					calButt.setForeground(Color.black);
				}
			} else if (e.getSource() == measButt) {
				measure();
			}

		}
	}
	
	private class MeasMouseListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (isMeasuring()) {
			}
		}
	}
}
