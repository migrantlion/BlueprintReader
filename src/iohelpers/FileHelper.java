package iohelpers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

public class FileHelper {
	
	private static String path = null;

	public static BufferedImage[] loadImages(JPanel window) {
		BufferedImage[] images;
		
		JFileChooser fileChooser = new JFileChooser();
		if (path == null)
			path = System.getProperty("user.home");
		fileChooser.setCurrentDirectory(new File(path));
		fileChooser.setMultiSelectionEnabled(true);
		
		int result = fileChooser.showOpenDialog(window);
		
		if (result == JFileChooser.APPROVE_OPTION) {
		    File[] selectedFiles = fileChooser.getSelectedFiles();
			
		    path = selectedFiles[0].getAbsolutePath();
		    images = new BufferedImage[selectedFiles.length];
			try {
				for (int i = 0; i < selectedFiles.length; i++)
				images[i] = ImageIO.read(selectedFiles[i]);
			} catch (IOException e) {
				System.out.println("Error in loading Image");
				e.printStackTrace();
			}
			return images;
		} else
			return null;
	}
}
