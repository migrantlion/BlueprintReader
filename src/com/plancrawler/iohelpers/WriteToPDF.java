package com.plancrawler.iohelpers;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import com.plancrawler.elements.DocumentHandler;
import com.plancrawler.elements.Item;
import com.plancrawler.elements.TakeOffManager;

public class WriteToPDF {

	public static void writeSummaryToPDF(TakeOffManager takeoff, DocumentHandler doc) {
		ArrayList<BufferedImage> images = PageImageOutput.getPageImagesWithMarks(takeoff, doc);
		BufferedImage summary = summaryImage(takeoff);
		
		if (summary == null)
			return;

		// choose filename
		JFileChooser chooser = new JFileChooser(doc.getCurrentPath());
		int returnVal = chooser.showSaveDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			if (chooser.getSelectedFile().getAbsolutePath().endsWith(".pdf"))
				writePDF(chooser.getSelectedFile(), summary, images);
			else {
				String filename = chooser.getSelectedFile().getAbsolutePath() + ".pdf";
				File file = new File(filename);
				writePDF(file, summary, images);
			}	
		}
	}

	private static BufferedImage summaryImage(TakeOffManager takeoff) {
		BufferedImage summary = new BufferedImage(3300, 2750, BufferedImage.TYPE_INT_RGB);
		Graphics g2 = summary.createGraphics();
		CopyOnWriteArrayList<Item> items = takeoff.getItems();
		Font currentFont = g2.getFont();
		Font newFont = currentFont.deriveFont(Font.BOLD, currentFont.getSize()*2f);
		g2.setFont(newFont);
		int xPos = 100;
		int yPos = 100;

		// fill image with white
		g2.setColor(Color.white);
		g2.fillRect(0, 0, summary.getWidth(), summary.getHeight());
		int iterator = 1;
		String line;
		FontMetrics metrics = g2.getFontMetrics(currentFont);
		for (Item i : items) {
			
			g2.setColor(Color.black);
			line = Integer.toString(iterator++)+".)";
			g2.drawString(line, xPos, yPos);
			
			xPos += 20 + metrics.stringWidth(line);
			
			g2.setColor(i.getColor());
			g2.fillOval(xPos, yPos-20, 20, 20);
			xPos += 40;
			
			g2.setColor(Color.black);
			line = i.toString();
			
			g2.drawString(line, xPos, yPos);			
			xPos = 100;
			yPos += 3 * metrics.getHeight();
		}
		
		// put the font back to what it was
		g2.setFont(currentFont);

		return summary;
	}

	private static void writePDF(File file, BufferedImage summary, ArrayList<BufferedImage> images) {
		// Create a document and add a page to it
		PDDocument document = new PDDocument();
		PDPage page = new PDPage(new PDRectangle(summary.getWidth(), summary.getHeight()));
		document.addPage(page);
		
		try {
			// Start a new content stream which will "hold" the to be created
			// content
			PDImageXObject img = LosslessFactory.createFromImage(document, summary);
			PDPageContentStream contentStream = new PDPageContentStream(document, page);
			contentStream.drawImage(img, 0, 0);
			contentStream.close();

			for (BufferedImage image : images) {
				page = new PDPage(new PDRectangle(image.getWidth(), image.getHeight()));
				document.addPage(page);
				img = LosslessFactory.createFromImage(document, image);
				contentStream = new PDPageContentStream(document, page);
				contentStream.drawImage(img, 0, 0);
				contentStream.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			document.save(file);
			document.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JOptionPane.showMessageDialog(null, "Completed PDF export: "+file.getAbsolutePath());
	}

}
