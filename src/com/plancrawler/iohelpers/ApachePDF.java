package com.plancrawler.iohelpers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

public class ApachePDF {

	public static ArrayList<BufferedImage> pdfToImage(String filename, int dpi) {
		ArrayList<BufferedImage> imageList = new ArrayList<>();

		try (PDDocument document = PDDocument.load(new File(filename))) {
			PDFRenderer pdfRenderer = new PDFRenderer(document);
			for (int page = 0; page < document.getNumberOfPages(); page++) {
				imageList.add(pdfRenderer.renderImageWithDPI(page, dpi, ImageType.GRAY));
			}
			document.close();
		} catch (IOException e) {
			// ignore it
			// e.printStackTrace();
		}

		return imageList;
	}

	public static int getPages(String filename) {
		int pages = 0;
		System.out.println("getting number of pages");
		try {
			PDDocument document = PDDocument.load(new File(filename));
			pages = document.getNumberOfPages();
			document.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return pages;
	}

	public static int getPages(File file) {
		int pages = 0;
		try {
			PDDocument document = PDDocument.load(file);
			pages = document.getNumberOfPages();
			document.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return pages;
	}

	public static void pdfWriteAllImages(String filename, int dpi) {
		BufferedImage image;

		try (PDDocument document = PDDocument.load(new File(filename + ".pdf"))) {
			PDFRenderer pdfRenderer = new PDFRenderer(document);
			for (int page = 0; page < document.getNumberOfPages(); page++) {
				String outfile = filename + "_p" + page + ".png";
				image = pdfRenderer.renderImageWithDPI(page, dpi, ImageType.GRAY);

				// write the image to file
				ImageIO.write(image, "png", new FileOutputStream(outfile));

			}
			document.close();
			JOptionPane.showMessageDialog(null, "Image output completed.");
		} catch (IOException e) {
			// ignore it
			// e.printStackTrace();
		}
	}

	public static BufferedImage pdfPageToImage(String filename, int page, int dpi) {
		BufferedImage pageImage = null;
		try (PDDocument document = PDDocument.load(new File(filename))) {
			PDFRenderer pdfRenderer = new PDFRenderer(document);

			if (page > document.getNumberOfPages())
				return null;

			pageImage = pdfRenderer.renderImageWithDPI(page, dpi, ImageType.GRAY);
			document.close();
		} catch (IOException e) {
			// TODO ignoring catch block
			// e.printStackTrace();
		}
		return pageImage;
	}

	public static void writeOutImage(BufferedImage image, String filename) {
		if (!filename.endsWith(".png"))
			filename = filename + ".png";
		
		if (image != null) {
			try {
				// write the image to file
				ImageIO.write(image, "png", new FileOutputStream(filename));
			} catch (IOException ex) {
				Logger.getLogger(ApachePDF.class.getName()).log(Level.SEVERE, null, ex);
			}

		}
	}
}
