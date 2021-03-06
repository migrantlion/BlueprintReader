package com.plancrawler.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import org.imgscalr.Scalr;

import com.plancrawler.utilities.MyPoint;

public class Screen extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final double MAXSCALE = 3.0;
	private double minScale = 0;

	private MyPoint origin = new MyPoint(0, 0);
	private double scale = 1;

	private BufferedImage image, originalImage;
	private int screenW, screenH;

	private CopyOnWriteArrayList<Paintable> marks = new CopyOnWriteArrayList<Paintable>();

	public Screen(int w, int h) {
		this.image = null;
		this.originalImage = null;
		this.screenW = w;
		this.screenH = h;
		this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		repaint();
	}

	public void setImage(BufferedImage image) {
		this.image = image;
		this.originalImage = image;
		scaleImage();
	}

	public void fitImage() {
		final double BORDER = 100.0;
		// update screen size in case was resized
		Dimension dim = this.getSize();
		screenW = dim.width;
		screenH = dim.height;

		if (image == null)
			return;
		else {
			image = originalImage;
			scale = ((double) (screenH + screenW - 2 * BORDER)) / ((double) (image.getHeight() + image.getWidth()));

			// System.out.println("scale " + scale + "... Width/Height = " +
			// screenW + "/" + screenH + " vs "
			// + (Math.floor(scale * originalImage.getWidth())));

			minScale = 0.5 * scale;
			origin.setTo(0., 0.);

			rescale(1, 0, 0);
			repaint();
		}
	}

	public void focus() {
		// replace the current image with a Quality scaled version from the
		// originalImage
		if (image != null) {
			image = Scalr.resize(originalImage, Scalr.Method.QUALITY,
					(int) (Math.floor(scale * originalImage.getWidth())),
					(int) (Math.floor(scale * image.getHeight())));
		}
		repaint();
	}
	
	public void quickFocus() {
		if (image != null) {
			image = Scalr.resize(originalImage, Scalr.Method.BALANCED,
					(int) (Math.floor(scale * originalImage.getWidth())),
					(int) (Math.floor(scale * image.getHeight())));
		}
		repaint();
	}

	public void move(int dx, int dy) {
		origin.translate(new MyPoint(dx, dy));
	}

	private void scaleImage() {
		// scale image to current scale setting
		if (image != null)
			image = Scalr.resize(originalImage, Scalr.Method.SPEED,
					(int) (Math.floor(scale * originalImage.getWidth())),
					(int) (Math.floor(scale * image.getHeight())));

		repaint();
	}

	public void rescale(double scalar, int screenX, int screenY) {
		// scale the image and center about x0 and y0 as we do so.
		double oldScale = scale;
		scale *= scalar;
		if (scale > MAXSCALE)
			scale = MAXSCALE;
		if (scale < minScale)
			scale = minScale;

		/*
		 * to find the new origins: (OrigX OrigY) = (1/s)(xI yI) = (1/s)[(sX sY)
		 * - (x0 y0)] this is the same point after scaling with a new scale s'.
		 * If want the screen coordinates to be the same, then: (1/s)[(sX sY) -
		 * (x0 y0)] = (1/s')[(sX sY) - (x0' y0')] solve for the new x0' and
		 * y0'...
		 */
		int newXorigin = (int) ((scale / oldScale) * origin.getX() - (scale / oldScale - 1) * screenX);
		int newYorigin = (int) ((scale / oldScale) * origin.getY() - (scale / oldScale - 1) * screenY);

		origin.setTo(newXorigin, newYorigin);

		scaleImage();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g2.setBackground(Color.WHITE);
		g2.clearRect(0, 0, this.getWidth(), this.getHeight());
		g2.setColor(Color.BLACK);

		paintImage(g2);
		paintMarks(g2);

		g2.dispose();
	}

	private void paintImage(Graphics2D g) {
		if (image != null)
			g.drawImage(image, (int) Math.floor(origin.getX()), (int) Math.floor(origin.getY()), null);
	}

	private void paintMarks(Graphics2D g2) {
		if (image != null) {
			for (Paintable m : marks) {
				m.paint(g2,  scale, origin);
			}
		}
	}

	public MyPoint getImageRelativePoint(MyPoint screenPoint) {
		// takes point on the screen and returns where it would be on the
		// original image.
		MyPoint imagePoint;

		// correct for origin shift and scale
		imagePoint = screenPoint.add(MyPoint.neg(origin));
		imagePoint.scale(1 / scale);

		return imagePoint;
	}

	public void displayMarks(CopyOnWriteArrayList<Paintable> marks) {
		this.marks = marks;
	}

}
