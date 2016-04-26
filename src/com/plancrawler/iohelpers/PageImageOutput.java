package com.plancrawler.iohelpers;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.plancrawler.elements.DocumentHandler;
import com.plancrawler.elements.Item;
import com.plancrawler.elements.Mark;
import com.plancrawler.elements.TakeOffManager;
import com.plancrawler.gui.Paintable;
import com.plancrawler.utilities.MyPoint;

public class PageImageOutput {

	public static void writePagesWithMarks(TakeOffManager takeoff, DocumentHandler doc) {

		String baseFileName;
		int numPages = doc.getNumPages();

		// choose filename
		JFileChooser chooser = new JFileChooser(doc.getCurrentPath());
		int returnVal = chooser.showSaveDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			baseFileName = chooser.getSelectedFile().getAbsolutePath();

			for (int page = 0; page < numPages; page++) {

				if (isMarksOnPage(takeoff, page)) {
					// render the page:
					BufferedImage image = doc.getPageImage(page);
					Graphics g = image.getGraphics();
					ArrayList<Paintable> marks = paintables(takeoff,page);
					for (Paintable p : marks) {
						p.paint(g, 1, new MyPoint(0,0));
					}
					//append page number to the file name
					String fileName = baseFileName + Integer.toString(page+1) + ".png";

					ApachePDF.writeOutImage(image, fileName);
				}
			}
			JOptionPane.showMessageDialog(null,"Completed export of all page images.");
			// reset the document to old page
		}
	}
	
	public static ArrayList<BufferedImage> getPageImagesWithMarks(TakeOffManager takeoff, DocumentHandler doc) {

		String baseFileName;
		int numPages = doc.getNumPages();
		ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
		
		// choose filename
		JFileChooser chooser = new JFileChooser(doc.getCurrentPath());
		int returnVal = chooser.showSaveDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			baseFileName = chooser.getSelectedFile().getAbsolutePath();
			if (!baseFileName.endsWith(".pdf"))
				baseFileName += ".pdf";

			
			for (int page = 0; page < numPages; page++) {

				if (isMarksOnPage(takeoff, page)) {
					// render the page:
					BufferedImage image = doc.getPageImage(page);
					Graphics g = image.getGraphics();
					ArrayList<Paintable> marks = paintables(takeoff,page);
					for (Paintable p : marks) {
						p.paint(g, 1, new MyPoint(0,0));
					}
					
					images.add(image);
				}
			}
		}
		return images;
	}


	public static boolean isMarksOnPage(TakeOffManager takeoff, int page) {
		ArrayList<Paintable> marks = new ArrayList<Paintable>();
		ArrayList<Item> items = new ArrayList<Item>();

		items.addAll(takeoff.getItems());

		for (Item i : items) {
			CopyOnWriteArrayList<Mark> ticks = i.getMarks(page);
			marks.addAll(ticks);
		}

		// add in the crates to display
		marks.addAll(takeoff.getShowroomMarks(page));
		marks.addAll(takeoff.getWarehouseMarks(page));

		if (marks.size() > 0)
			return true;
		else
			return false;
	}
	
	private static ArrayList<Paintable> paintables(TakeOffManager takeoff, int page){
		ArrayList<Paintable> marks = new ArrayList<Paintable>();
		ArrayList<Item> items = new ArrayList<Item>();

		items.addAll(takeoff.getItems());

		for (Item i : items) {
			CopyOnWriteArrayList<Mark> ticks = i.getMarks(page);
			marks.addAll(ticks);
		}

		// add in the crates to display
		marks.addAll(takeoff.getShowroomMarks(page));
		marks.addAll(takeoff.getWarehouseMarks(page));
		
		return marks;
	}
}
