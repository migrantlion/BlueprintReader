package com.plancrawler.guiComponents;

import java.awt.Color;

import javax.swing.JColorChooser;
import javax.swing.JComponent;

import com.plancrawler.elements.ColorSettings;

/*  This class creates a dialog which allows the user to enter in the required information to
 *  create a ColorSettings object.
 */

public class ColorPropertyDialog {

	public static ColorSettings pickNewColor(JComponent component, ColorSettings colorSetting) {
		
		Color color = JColorChooser.showDialog(component, "Pick a Color", colorSetting.getFillColor());
		colorSetting.setFillColor(color);
		return colorSetting;
	}
	
}
