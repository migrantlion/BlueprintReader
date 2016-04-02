package images;

import java.awt.image.BufferedImage;
import java.io.File;

import pdf.ApachePDF;

public class DocLoader implements Runnable {

	private File file;
	private final int DPI = 100;
	
	public DocLoader(File file) {
		this.file = file;
	}
	
	private BufferedImage[] loadPDFImages() {
		String pdfPath = file.getAbsolutePath();
		
		int numpages = ApachePDF.getPages(file);
		
		BufferedImage[] pageImages = new BufferedImage[numpages];
		for (int i = 0; i < numpages; i++)
			pageImages[i] = ApachePDF.pdfPageToImage(pdfPath, i, DPI);
		
		return pageImages;
	}
	
	
	@Override
	public void run() {
		
		
	}

}
