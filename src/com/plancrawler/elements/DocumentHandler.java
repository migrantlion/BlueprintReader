package com.plancrawler.elements;

import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

public class DocumentHandler implements Serializable {

	private static final long serialVersionUID = 1L;

	private transient PDDocument document;
	private String currentFile;
	private String path = "C:\\Users\\Steve.Soss\\Documents\\PlanCrawler\\Plans\\";
	private int numPages, currentPage;
	private final int DPI = 300;
	private HashMap<Integer, Double> pageRotations;
	private HashMap<Integer, Double> pageCalibration;

	public DocumentHandler(JFrame mainWindow) {
		this.numPages = 0;
		this.currentPage = 0;
		this.pageRotations = new HashMap<Integer, Double>();
		this.pageCalibration = new HashMap<Integer, Double>();

		mainWindow.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (document != null)
					try {
						document.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				mainWindow.setVisible(false);
			}
		});
	}

	public void registerCalibration(double scale) {
		pageCalibration.put(currentPage, scale);
	}

	public BufferedImage getCurrentPageImage() {
		return getPageImage(currentPage);
	}

	public BufferedImage getNextPageImage() {
		return getPageImage(currentPage + 1);
	}

	public BufferedImage getPrevPageImage() {
		return getPageImage(currentPage - 1);
	}

	private BufferedImage checkRotations(BufferedImage image) {
		if (pageRotations.containsKey(currentPage)) {
			double radians = pageRotations.get(currentPage);

			// need to get the new center of the rotated image
			int w = image.getWidth();
			int h = image.getHeight();

			// define transformation
			AffineTransform t = new AffineTransform();
			t.rotate(radians, w / 2d, h / 2d);

			// rectangle around source image
			Point[] points = { new Point(0, 0), new Point(w, 0), new Point(w, h), new Point(0, h) };
			// transform to destination rectangle
			t.transform(points, 0, points, 0, 4);

			// now get destination box center min/max x and y
			Point min = new Point(points[0]);
			Point max = new Point(points[0]);
			for (int i = 1, n = points.length; i < n; i++) {
				Point p = points[i];
				double pX = p.getX(), pY = p.getY();

				// update min/max x
				if (pX < min.getX())
					min.setLocation(pX, min.getY());
				if (pX > max.getX())
					max.setLocation(pX, max.getY());

				// update min/max y
				if (pY < min.getY())
					min.setLocation(min.getX(), pY);
				if (pY > max.getY())
					max.setLocation(max.getX(), pY);
			}

			// determine new width, height
			w = (int) (max.getX() - min.getX());
			h = (int) (max.getY() - min.getY());

			// determine required translation
			double tx = min.getX();
			double ty = min.getY();

			// append required translation
			AffineTransform translation = new AffineTransform();
			translation.translate(-tx, -ty);
			t.preConcatenate(translation);

			AffineTransformOp op = new AffineTransformOp(t, AffineTransformOp.TYPE_BILINEAR);

			BufferedImage rotImage = new BufferedImage(w, h, image.getType());
			op.filter(image, rotImage);
			return rotImage;
		} else
			return image;
	}

	public void registerRotation(double radians) {
		if (pageRotations.containsKey(currentPage))
			pageRotations.put(currentPage, pageRotations.get(currentPage) + radians);
		else
			pageRotations.put(currentPage, radians);
	}

	public BufferedImage getPageImage(int pageNum) {
		BufferedImage image = null;
		if (document == null)
			loadDocument();

		if (numPages <= 0)
			return null;
		if (pageNum >= numPages)
			pageNum = numPages - 1;
		if (pageNum <= 0)
			pageNum = 0;
		currentPage = pageNum;

		PDFRenderer pdfRenderer = new PDFRenderer(document);
		try {
			image = pdfRenderer.renderImageWithDPI(currentPage, DPI);
			image = checkRotations(image);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}

	public String loadPDF() {
		currentFile = null;
		document = new PDDocument();
		File pdfFile = chooseFile();
		if (pdfFile != null) {
			try {
				currentFile = pdfFile.getAbsolutePath();
				document = PDDocument.load(pdfFile);
				numPages = document.getNumberOfPages();
				currentPage = 0;
			} catch (FileNotFoundException ex) {
				System.out.println("Error file not found " + ex);
			} catch (IOException ex) {
				System.out.println("Error IOException " + ex);
			}
		}
		return currentFile;
	}
	
	private void loadDocument() {
		if (currentFile == null)
			loadPDF();
		else {
			document = new PDDocument();
			File pdfFile = new File(currentFile);
			if (pdfFile != null) {
				try {
					currentFile = pdfFile.getPath();
					document = PDDocument.load(pdfFile);
					numPages = document.getNumberOfPages();
					currentPage = 0;
				} catch (FileNotFoundException ex) {
					System.out.println("Error file not found " + ex);
				} catch (IOException ex) {
					System.out.println("Error IOException " + ex);
				}
			}
		}
	}

	private File chooseFile() {
		JFileChooser fileChooser = new JFileChooser();

		if (path == null)
			path = System.getProperty("user.home");

		fileChooser.setCurrentDirectory(new File(path));
		fileChooser.setMultiSelectionEnabled(false);

		int result = fileChooser.showOpenDialog(null);

		if (result == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			path = selectedFile.getAbsolutePath();
		} else
			return null;

		return new File(path);
	}

	public int getNumPages() {
		return numPages;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public double getCalibration() {
		if (pageCalibration.containsKey(currentPage))
			return pageCalibration.get(currentPage);
		else
			return 1.0;
	}

	public void registerRotationToAllPages() {
		if (pageRotations.containsKey(currentPage)) {
			double rotation = pageRotations.get(currentPage);
			for (int i = 0; i < numPages; i++)
				pageRotations.put(i, rotation);
		}
	}
}
