package images;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JPanel;

import iohelpers.FileHelper;
import pdf.ApachePDF;

public class DocumentImages {

	private final int DPI = 300;
	
	private BufferedImage image;
	private String pdfPath = null;
	private int numPages = 0;
	private int currentPage = 0;
	
	public void loadPDF (JPanel panel) {
		
		File pdfFile = FileHelper.getFile(panel);
		if (pdfFile == null)
			return;
		
		pdfPath = pdfFile.getAbsolutePath();
		numPages = ApachePDF.getPages(pdfFile);
		image = getPageImage(0);
		
	}
	
	public BufferedImage getPageImage(int pagenum) {
		if ((pdfPath == null) || (numPages == 0))
			return null;
		if (pagenum >= numPages)
			pagenum = numPages-1;
		if (pagenum <= 0)
			pagenum = 0;
		currentPage = pagenum;
		image = ApachePDF.pdfPageToImage(pdfPath, currentPage, DPI);
		return image;
	}
	
	public BufferedImage getPrevPageImage() {
		return getPageImage(currentPage - 1);
	}

	public BufferedImage getNextPageImage() {
		return getPageImage(currentPage + 1);
	}
	
	public BufferedImage getCurrentPageImage() {
		return image;
	}

	public void loadImage (JPanel panel) {
		pdfPath = null;
		numPages = 1;
		currentPage = 0;
		image = FileHelper.loadImage(panel);
	}

	public int getNumPages() {
		return numPages;
	}
	
	public int getCurrentPage() {
		return currentPage;
	}
	
}
