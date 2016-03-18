package visuals;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JColorChooser;

public class DrawProperties {

	private static final Color defaultColor = Color.RED;
	
	private static float opacity = 1.0f;
	private static Color color = defaultColor;
	private static boolean fill = false;
	
	public static void setOpacity(double val) {
		opacity = (float)(val);
	}
	
	public static float getOpacity() {
		return opacity;
	}
	
	public static void pickColor(Component component) {
		color = JColorChooser.showDialog(component, "Pick a Color", defaultColor);
	}
	
	public static void setFill(boolean state) {
		fill = state;
	}

	public static Color getColor() {
		return color;
	}

	public static void setColor(Color color) {
		DrawProperties.color = color;
	}

	public static Color getDefaultcolor() {
		return defaultColor;
	}

	public static boolean isFill() {
		return fill;
	}

} 
