package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.JPanel;

import org.imgscalr.Scalr;

import visuals.Marker;


public class Screen extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final int MAXSCALE = 3;
	
	private int xOrigin = 0, yOrigin = 0;
	private double scale = 1;
	
	private BufferedImage image, originalImage;
	private int screenW, screenH;
	private ArrayList<Marker> marks;

	public Screen(int w, int h){
		this.image = null;
		this.originalImage = null;
		this.screenW = w;
		this.screenH = h;
		repaint();
		this.marks = new ArrayList<Marker>();
	}
	
	public void setImage(BufferedImage image){
		this.image = image;
		this.originalImage = image;
		fitImage();
	}
	
	public void fitImage(){
		final double BORDER = 40.0;
		if (image == null)
			return;
		else {
			System.out.println("Image width/height: "+image.getWidth()+", "+image.getHeight());
			if (image.getWidth()/screenW < image.getHeight())
				scale = ((double) screenW - 2*BORDER)/((double)(image.getWidth()));
			else
				scale = ((double) screenH - 2*BORDER)/((double)(image.getHeight()));
			System.out.println("scale "+scale+"... Width/Height = "+screenW+"/"+screenH+" vs "+(Math.floor(scale*originalImage.getWidth())));
			image = Scalr.resize(originalImage, Scalr.Method.QUALITY, 
					(int) (Math.floor(scale*originalImage.getWidth())), (int) (Math.floor(scale*image.getHeight())));
			originalImage = image;
			xOrigin = 0;
			yOrigin = 0;
			scale = 1;
			repaint();
		}
	}
	
	public void focus() {
		// replace the current image with a Quality scaled version from the originalImage
		if (image != null)
			image = Scalr.resize(originalImage, Scalr.Method.QUALITY, 
					(int) (Math.floor(scale*originalImage.getWidth())), 
					(int) (Math.floor(scale*image.getHeight())));
		repaint();
	}
	
	public void move(int dx, int dy){
		this.xOrigin += dx;
		this.yOrigin += dy;
	}
	
	public void addMark(Marker mark){
		mark.move(-xOrigin, -yOrigin);
		marks.add(mark);
	}
	
	public void rescale(double scalar, int x0, int y0) {
		// scale the image and center about x0 and y0 as we do so.
		scalar = scale*scalar;
		if (scalar > MAXSCALE)
			scalar = MAXSCALE;
		
		int deltaW = (int)(Math.floor((scale - scalar)*(x0)));
		int deltaH = (int)(Math.floor((scale - scalar)*(y0)));
		this.scale = scalar;
		move(deltaW,deltaH);
		if (image != null)
			image = Scalr.resize(originalImage, Scalr.Method.SPEED, 
					(int) (Math.floor(scale*originalImage.getWidth())), 
					(int) (Math.floor(scale*image.getHeight())));
		
		for (Marker m : marks){
			m.move(deltaW, deltaH);
			m.scale(scale);
		}
		repaint();
	}
		
	public void paint(Graphics g){
		g.setColor(Color.WHITE);
		g.fillRect(-screenW, -screenH, 3*screenW, 3*screenH);
		g.setColor(Color.BLACK);
		if (image != null)
			g.drawImage(image, xOrigin, yOrigin, null);
		for (Marker m : marks){
			g.setColor(m.getColor());
			g.drawRect(m.getX()+xOrigin, m.getY()+yOrigin, m.getWidth(), m.getHeight());
		}
	}

}
