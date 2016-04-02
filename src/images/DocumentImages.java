package images;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;

import iohelpers.FileHelper;
import pdf.ApachePDF;

public class DocumentImages implements Runnable, Serializable {

	private static final long serialVersionUID = -620726274807841575L;

	private final int DPI = 100;
	
	private final String loadImgPath = "res/loading.png";
	private BufferedImage image, loading;
	private BufferedImage[] imageBuffer;
	private String pdfPath = null;
	private int numPages = 0;
	private int currentPage = 0;
	
	public DocumentImages() {
		loading = getLoadingImage();
	}
	
	private BufferedImage getLoadingImage() {
		BufferedImage image = null;
		
		File file = new File(loadImgPath);
		try {
			image = ImageIO.read(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return image;
	}
	
	public void loadPDF (JPanel panel) {
		
		File pdfFile = FileHelper.getFile(panel);
		if (pdfFile == null)
			return;

		System.out.println("getting file");
		pdfPath = pdfFile.getAbsolutePath();
		numPages = ApachePDF.getPages(pdfFile);
		
		imageBuffer = new BufferedImage[numPages];
		imageBuffer[0] = loadPageImage(0);
		
		for (int i = 1; i < numPages; i++)
			imageBuffer[i] = loading;
		Thread loader = new Thread(this);
		loader.start();
	}
	
	private void finishLoadingPDF() {		
		for (int n = 1; n < numPages; n++) {
			imageBuffer[n] = ApachePDF.pdfPageToImage(pdfPath, n, DPI);
		}
	}
	
	private BufferedImage loadPageImage(int pagenum) {
		if (numPages <= 0)
			return null;
		if (pagenum >= numPages)
			pagenum = numPages-1;
		if (pagenum <= 0)
			pagenum = 0;
		currentPage = pagenum;
		System.out.println("reading page");
		image = ApachePDF.pdfPageToImage(pdfPath, currentPage, DPI);
		return image;
	}
	
	public BufferedImage getPageImage(int pagenum) {
		if (numPages <= 0)
			return null;
		if (pagenum >= numPages)
			pagenum = numPages-1;
		if (pagenum <= 0)
			pagenum = 0;
		currentPage = pagenum;
		image = imageBuffer[currentPage];
		return image;
	}

	
	public BufferedImage getPage(int pagenum) {
		if (numPages <= 0)
			return null;
		if (pagenum >= numPages)
			pagenum = numPages-1;
		if (pagenum <= 0)
			pagenum = 0;
		currentPage = pagenum;
		image = imageBuffer[currentPage];
		return image;
	}
	
	public BufferedImage getPrevPageImage() {
		return getPage(currentPage - 1);
	}

	public BufferedImage getNextPageImage() {
		return getPage(currentPage + 1);
	}
	
	public BufferedImage getCurrentPageImage() {
		return image;
	}

	public void loadImages (JPanel panel) {
		pdfPath = null;
		currentPage = 0;
		imageBuffer = FileHelper.loadImages(panel);
		image = imageBuffer[currentPage];
		numPages = imageBuffer.length;
	}

	public int getNumPages() {
		return numPages;
	}
	
	public int getCurrentPage() {
		return currentPage;
	}

	@Override
	public void run() {
		finishLoadingPDF();
		
	}
	
}
