package com.plancrawler.gui;

import java.awt.Component;
import java.awt.Window;

import javax.swing.JOptionPane;

public class GeneralGUITools {

	public static Window findWindow(Component c) {
		if (c == null) {
			return JOptionPane.getRootFrame();
		} else if (c instanceof Window) {
			return (Window) c;
		} else {
			return findWindow(c.getParent());
		}
	}
}
