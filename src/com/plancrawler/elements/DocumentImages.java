package com.plancrawler.elements;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import com.plancrawler.iohelpers.ApachePDF;
import com.plancrawler.iohelpers.FileHelper;

////public class DocumentImages implements Runnable, Serializable {
////
////	private static final long serialVersionUID = -620726274807841575L;
////
////	private final int DPI = 100;
////
////	private final String loadImgPath = "res/loading.png";
////	private transient BufferedImage image, loading;
////	private transient BufferedImage[] imageBuffer;
////	private String pdfPath = null;
////	private int numPages = 0;
////	private int currentPage = 0;
////	private boolean isPDFFile = false;
////
////	public DocumentImages() {
////		loading = getLoadingImage();
////	}
////
////	private BufferedImage getLoadingImage() {
////		BufferedImage image = null;
////
////		File file = new File(loadImgPath);
////		try {
////			image = ImageIO.read(file);
////		} catch (IOException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
////		return image;
////	}
////
////	public void reloadPDF() {
////		getLoadingImage();
////		imageBuffer = new BufferedImage[numPages];
////
////		if (isPDFFile) {
////			imageBuffer[0] = loadPageImage(0);
////			imageBuffer[currentPage] = loadPageImage(currentPage);
////			image = imageBuffer[currentPage];
////			backgroundLoadPDF();
////		}
////	}
////
////	public void loadPDF(JPanel panel) {
////
////		File pdfFile = FileHelper.getFile(panel);
////		if (pdfFile == null)
////			return;
////
////		pdfPath = pdfFile.getAbsolutePath();
////		isPDFFile = true;
////		numPages = ApachePDF.getPages(pdfFile);
////
////		imageBuffer = new BufferedImage[numPages];
////		backgroundLoadPDF();
////	}
////
////	public void backgroundLoadPDF() {
////		imageBuffer[0] = loadPageImage(0);
////		for (int i = 1; i < numPages; i++)
////			imageBuffer[i] = loading;
////		Thread loader = new Thread(this);
////		loader.start();
////	}
////
////	private void finishLoadingPDF() {
////		for (int n = 0; n < numPages; n++) {
////			imageBuffer[n] = ApachePDF.pdfPageToImage(pdfPath, n, DPI);
////		}
////	}
////
////	private BufferedImage loadPageImage(int pagenum) {
////		if (numPages <= 0)
////			return null;
////		if (pagenum >= numPages)
////			pagenum = numPages - 1;
////		if (pagenum <= 0)
////			pagenum = 0;
////		currentPage = pagenum;
////		System.out.println("reading page");
////		image = ApachePDF.pdfPageToImage(pdfPath, currentPage, DPI);
////		return image;
////	}
////
////	public BufferedImage getPageImage(int pagenum) {
////		if (numPages <= 0)
////			return null;
////		if (pagenum >= numPages)
////			pagenum = numPages - 1;
////		if (pagenum <= 0)
////			pagenum = 0;
////		if (currentPage != pagenum) {
////			currentPage = pagenum;
////			if (imageBuffer[currentPage].equals(loading))
////				image = loadPageImage(currentPage);
////			else
////				image = imageBuffer[currentPage];
////		}
////		return image;
////	}
////
////
////	public BufferedImage getPrevPageImage() {
////		return getPageImage(currentPage - 1);
////	}
////
////	public BufferedImage getNextPageImage() {
////		return getPageImage(currentPage + 1);
////	}
////
////	public BufferedImage getCurrentPageImage() {
////		// if (imageBuffer[currentPage].equals(loading))
////		// image = loadPageImage(currentPage);
////		// else
////		// image = imageBuffer[currentPage];
////		return getPageImage(currentPage);
////	}
////
////	public BufferedImage getCurrentPageImage(int DPI) {
////		if ((DPI != this.DPI) && isPDFFile) {
////			image = ApachePDF.pdfPageToImage(pdfPath, currentPage, DPI);
////			imageBuffer[currentPage] = image;
////		}
////		return image;
////	}
////
////	public void loadImages(JPanel panel) {
////		imageBuffer = null;
////		pdfPath = null;
////		currentPage = 0;
////		imageBuffer = FileHelper.loadImages(panel);
////		image = imageBuffer[currentPage];
////		numPages = imageBuffer.length;
////	}
////
////	public int getNumPages() {
////		return numPages;
////	}
////
////	public int getCurrentPage() {
////		return currentPage;
////	}
////
////	public boolean isPDFFile() {
////		return isPDFFile;
////	}
////
////	@Override
////	public void run() {
////		finishLoadingPDF();
////
////	}
//
//}
