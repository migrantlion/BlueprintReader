package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputListener;

import images.DocumentImages;
import visuals.Marker;

public class GUI extends JFrame{
	// main window which holds the menu, screen area, buttons
	private static final long serialVersionUID = 1L;
	
	private int width = 1400;
	private int height = (int) Math.floor(width*(9.0/16.0));
	private int bottomMargin = 200;
	private int sideMargin = 100;
	
	private DocumentImages docImages = new DocumentImages();
	JSlider pageSlider;
	JLabel pageLabel;
	
	private Screen centerScreen;
	
	private SlideListener slideListener = new SlideListener();
//	private ActiveDetailPanel activeDetailPanel;
//	private DetailInfoPanel detailInfoPanel;
//	private MarkInfoPanel markInfoPanel;
	
	public GUI() {
		super("PlanCrawler Blueprint Reader");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		setSize(width,height);
	}
	
	public void init() {
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension dim = tk.getScreenSize();
		int xpos = (int) (0.5*(dim.width - width));
		int ypos = (int) (0.5*(dim.height - height));
		setLocation(xpos,ypos);
		
		attachMenu();
		attachBottomButtons();
		attachCenterScreen();
		
		setVisible(true);
	}
	
	private void attachCenterScreen() {
		MouseHandler mouseHandler = new MouseHandler();
		centerScreen = new Screen(width - 2*sideMargin,height - bottomMargin);
		centerScreen.addMouseListener(mouseHandler);
		centerScreen.addMouseWheelListener(mouseHandler);
		centerScreen.addMouseMotionListener(mouseHandler);
		
		this.add(centerScreen, BorderLayout.CENTER);
	}
	
	private void attachBottomButtons() {
		Box buttonBox = Box.createHorizontalBox();
		JButton focusButt, fitButt, prevButt, nextButt;
	
		pageSlider = new JSlider(docImages.getCurrentPage(),1,docImages.getNumPages());
		pageSlider.addChangeListener(slideListener);
		
		pageLabel = new JLabel("   Page: "+docImages.getCurrentPage()+" of "+docImages.getNumPages());

		focusButt = makeButton("FOCUS", "FOCUS");
		fitButt = makeButton("FIT", "FIT_TO_SCREEN");
		prevButt = makeButton("<|", "PREV_IMAGE");
		nextButt = makeButton("|>", "NEXT_IMAGE");
		
		buttonBox.add(focusButt);
		buttonBox.add(fitButt);
		buttonBox.add(prevButt);
		buttonBox.add(pageLabel);
		buttonBox.add(pageSlider);
		buttonBox.add(nextButt);
		
		this.add(buttonBox, BorderLayout.SOUTH);	
	}
	
	private void updateBottomButtons() {
		pageSlider.setMaximum(docImages.getNumPages());
		pageSlider.setValue(docImages.getCurrentPage()+1);
		pageLabel.setText("   Page: "+Integer.toString(docImages.getCurrentPage()+1)+" of "+docImages.getNumPages());
	}
	
	private JButton makeButton(String iconFile, String actionName) {
		JButton theButton = new JButton();
		theButton.setText(iconFile);
		//TODO:  change this from text to getting an icon
		// Icon buttIcon = new ImageIcon(iconFile);
		// theButton.setIcon(buttIcon);
		
		theButton.setActionCommand(actionName);
		theButton.addActionListener(new ButtonListener());
		return theButton;
	}
	
	private void attachMenu() {
		JMenuBar menuBar = new JMenuBar();
		
		// define the menus 
		JMenu fileMenu = new JMenu("File");
		JMenu aboutMenu = new JMenu("About");
		JMenu editMenu = new JMenu("Edit");
		
		MenuItemListener menuItemListener = new MenuItemListener();
		
		// define menu items
		JMenuItem loadPDFMenuItem = new JMenuItem("Load PDF");
		loadPDFMenuItem.setActionCommand("LOAD_PDF");
		loadPDFMenuItem.addActionListener(menuItemListener);
		
		JMenuItem loadPicsMenuItem = new JMenuItem("Load Images");
		loadPicsMenuItem.setActionCommand("LOAD_IMAGES");
		loadPicsMenuItem.addActionListener(menuItemListener);
		
		JMenuItem saveMenuItem = new JMenuItem("Save");
		saveMenuItem.setActionCommand("SAVE");
		saveMenuItem.addActionListener(menuItemListener);
		
		JMenuItem exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.setActionCommand("EXIT");
		exitMenuItem.addActionListener(menuItemListener);
		
		JMenuItem aboutMenuItem = new JMenuItem("About");
		aboutMenuItem.setActionCommand("ABOUT");
		aboutMenuItem.addActionListener(menuItemListener);
		
		fileMenu.add(loadPDFMenuItem);
		fileMenu.add(loadPicsMenuItem);
		fileMenu.add(saveMenuItem);
		fileMenu.add(exitMenuItem);
		
		aboutMenu.add(aboutMenuItem);
		
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(aboutMenu);
		
		this.setJMenuBar(menuBar);
	}
	
	private class ButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			switch (e.getActionCommand()) {
			case "FOCUS": 
				centerScreen.focus();
				break;
			case "PREV_IMAGE":
				centerScreen.setImage(docImages.getPrevPageImage());
				updateBottomButtons();
				break;
			case "NEXT_IMAGE":
				centerScreen.setImage(docImages.getNextPageImage());
				updateBottomButtons();
				break;
			case "FIT_TO_SCREEN":
				centerScreen.fitImage();
			default:
				System.out.println("Not sure what this button does!");	
			}
		}
		
	}
	
	private class SlideListener implements ChangeListener {

		@Override
		public void stateChanged(ChangeEvent e) {
			if (e.getSource() == pageSlider) {
				int newpage = pageSlider.getValue()-1;
				centerScreen.setImage(docImages.getPageImage(newpage));
				updateBottomButtons();
			}
		}
		
	}
	
	private class MenuItemListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			switch (e.getActionCommand()) {
			case "LOAD_PDF":
				docImages.loadPDF(centerScreen);
				centerScreen.setImage(docImages.getCurrentPageImage());
				updateBottomButtons();
				break;	
			case "LOAD_IMAGES":
				docImages.loadImage(centerScreen);
				centerScreen.setImage(docImages.getCurrentPageImage());
				updateBottomButtons();
				break;
			case "EXIT":
				System.exit(NORMAL);
				break;
			default:
				System.out.println("Didn't choose the load PDF");
			}
			System.out.println(e.getActionCommand() + " menu item clicked!");
		}

	}
	
	private class MouseHandler implements MouseWheelListener, MouseInputListener, MouseMotionListener {
		private int mouseX, mouseY;
		private boolean needsFocus = false;
		
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			double notches = e.getWheelRotation();
			centerScreen.rescale((1 + notches / 10), e.getX(), e.getY());
			// needsFocus = true;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			System.out.println("Mouse clicked!");
			int box = 40;
			Marker mark = new Marker(e.getX()-box, e.getY()-box, 2*box, 2*box, Color.GREEN);
			centerScreen.addMark(mark);
			centerScreen.repaint();
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
			int dX = e.getX() - mouseX;
			int dY = e.getY() - mouseY;

			mouseX = e.getX();
			mouseY = e.getY();
			
			centerScreen.move(dX, dY);
			centerScreen.repaint();
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			mouseX = e.getX();
			mouseY = e.getY();
			if (needsFocus) {
				centerScreen.focus();
				needsFocus = false;
			}
		}
	}
}
