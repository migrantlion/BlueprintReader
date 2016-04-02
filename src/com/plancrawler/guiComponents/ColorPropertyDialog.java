package com.plancrawler.guiComponents;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JColorChooser;

import com.plancrawler.elements.ColorSettings;

/*  This class creates a dialog which allows the user to enter in the required information to
 *  create a ColorSettings object.
 */

public class ColorPropertyDialog {

	public static ColorSettings pickNewColor(ColorSettings colorSetting) {
		
		Color color = JColorChooser.showDialog(null, "Pick a Color", Color.BLACK);
		colorSetting.setFillColor(color);
		colorSetting.setLineColor(color);
		return colorSetting;
	}
	
	public static void pickColor(Component component) {
		
	}
}
