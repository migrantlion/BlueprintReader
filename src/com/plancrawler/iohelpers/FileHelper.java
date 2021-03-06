package com.plancrawler.iohelpers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

public class FileHelper {

	private static String path = "C:\\Users\\Steve.Soss\\Documents\\PlanCrawler\\Plans\\";

	public static File[] getFiles(JPanel window) {
		JFileChooser fileChooser = new JFileChooser();

		if (path == null)
			path = System.getProperty("user.home");

		fileChooser.setCurrentDirectory(new File(path));
		fileChooser.setMultiSelectionEnabled(true);

		int result = fileChooser.showOpenDialog(window);

		if (result == JFileChooser.APPROVE_OPTION) {
			File[] selectedFiles = fileChooser.getSelectedFiles();
			path = selectedFiles[0].getAbsolutePath();
			return selectedFiles;
		} else
			return null;
	}

	public static File getFile(JPanel panel) {
		JFileChooser fileChooser = new JFileChooser();

		if (path == null)
			path = System.getProperty("user.home");

		fileChooser.setCurrentDirectory(new File(path));
		fileChooser.setMultiSelectionEnabled(false);

		int result = fileChooser.showOpenDialog(panel);

		if (result == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			path = selectedFile.getAbsolutePath();
			return selectedFile;
		} else
			return null;
	}

	public static BufferedImage loadImage(JPanel panel) {
		BufferedImage image = null;

		JFileChooser fileChooser = new JFileChooser();
		if (path == null)
			path = System.getProperty("user.home");
		fileChooser.setCurrentDirectory(new File(path));
		fileChooser.setMultiSelectionEnabled(false);

		int result = fileChooser.showOpenDialog(panel);

		if (result == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();

			path = selectedFile.getAbsolutePath();
			try {
				image = ImageIO.read(selectedFile);
			} catch (IOException e) {
				System.out.println("Error in loading Image");
				e.printStackTrace();
			}
		}
		return image;
	}

	public static BufferedImage[] loadImages(JPanel window) {
		BufferedImage[] images = new BufferedImage[1];
		images[0] = null;

		JFileChooser fileChooser = new JFileChooser();
		if (path == null)
			path = System.getProperty("user.home");
		fileChooser.setCurrentDirectory(new File(path));
		fileChooser.setMultiSelectionEnabled(true);

		int result = fileChooser.showOpenDialog(window);

		if (result == JFileChooser.APPROVE_OPTION) {
			File[] selectedFiles = fileChooser.getSelectedFiles();

			path = selectedFiles[0].getAbsolutePath();
			System.out.println(path);
			images = new BufferedImage[selectedFiles.length];
			try {
				for (int i = 0; i < selectedFiles.length; i++)
					images[i] = ImageIO.read(selectedFiles[i]);
			} catch (IOException e) {
				System.out.println("Error in loading Image");
				e.printStackTrace();
			}
		}
		return images;
	}

}
