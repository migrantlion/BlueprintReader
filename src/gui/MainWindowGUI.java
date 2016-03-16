package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.event.MouseInputListener;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;

import visuals.Marker;

public class MainWindowGUI extends JFrame {
	private static final long serialVersionUID = 1L;
	private static int WIDTH = 1820;
	private static int HEIGHT = 1080;
	
	private int iWidth, iHeight;
	int iX = 0, iY = 0;

	private Screen imagePanel;
	private MyMenu menu;
	private MHandler mhandler;

	public MainWindowGUI() {
		super("PlanCrawler Blueprint reader");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		ImageIcon logoIcon = new ImageIcon("./res/icons/Spider.png");
		Image logo = logoIcon.getImage();
		this.setIconImage(logo);

		imagePanel = new Screen(WIDTH, HEIGHT);
		imagePanel.setImage(loadImage());
		imagePanel.fitImage();
		
		this.mhandler = new MHandler();
		imagePanel.addMouseWheelListener(mhandler);
		imagePanel.addMouseMotionListener(mhandler);
		imagePanel.addMouseListener(mhandler);

		this.menu = new MyMenu();
		
		this.add(imagePanel, BorderLayout.CENTER);
		this.setJMenuBar(menu.menuBar);
	}
	
	public void initialize(){
		setSize(WIDTH, HEIGHT);
		setVisible(true);
	}

	private BufferedImage loadImage() {
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File("./res/test.png"));
			int imageW = image.getWidth();
			int imageH = image.getHeight();
			double iscale = imageW / imageH;
			this.iWidth = WIDTH;
			this.iHeight = (int) (HEIGHT / iscale);

			image = Scalr.resize(image, Method.BALANCED, iWidth, iHeight);
		} catch (IOException e) {
			System.out.println("Error in loading Image");
			e.printStackTrace();
		}
		return image;
	}

//	public void rescaleImage(double scalar, int x0, int y0) {
//		// scale the image and center about x0 and y0 as we do so.
//		if (scalar > MAXSCALE)
//			scalar = MAXSCALE;
//		
//		int deltaW = (int)(Math.floor((scale - scalar)*(x0)));
//		int deltaH = (int)(Math.floor((scale - scalar)*(y0)));
//		this.scale = scalar;
//		iX += deltaW;
//		iY += deltaH;
//		image = backupImage;
//		image = Scalr.resize(image, Scalr.Method.QUALITY, (int) (Math.floor(scale*iWidth)), (int) (Math.floor(scale*iHeight)));
//		imagePanel.setImage(image);
//		imagePanel.moveTo(iX, iY);
//		imagePanel.repaint();
//	}

	private class MHandler implements MouseWheelListener, MouseInputListener {
		private int mouseX, mouseY;
		
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			double notches = e.getWheelRotation();
//			System.out.println("scale = " + scale);
//			System.out.println("Wheel moved " + notches + " clicks.  "  + (notches / 10));
			imagePanel.rescale((1 + notches / 10), e.getX(), e.getY());
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			System.out.println("Mouse clicked!");
			int box = 40;
			Marker mark = new Marker(e.getX()-box, e.getY()-box, 2*box, 2*box, Color.GREEN);
			imagePanel.addMark(mark);
			imagePanel.repaint();
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseDragged(MouseEvent e) {
//			System.out.println("Mouse is being dragged!  "+e.getX()+", "+e.getY());
			int dX = e.getX() - mouseX;
			int dY = e.getY() - mouseY;

//			iX += dX;
//			iY += dY;
			mouseX = e.getX();
			mouseY = e.getY();
			
			imagePanel.move(dX, dY);
			imagePanel.repaint();
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			mouseX = e.getX();
			mouseY = e.getY();
//			System.out.println("mouse is moving "+mouseX+", "+mouseY);
		}
	}
}
