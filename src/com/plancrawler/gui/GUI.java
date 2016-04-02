package com.plancrawler.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputListener;

import com.plancrawler.elements.DocumentImages;
import com.plancrawler.elements.Item;
import com.plancrawler.elements.Mark;
import com.plancrawler.elements.TakeOffManager;
import com.plancrawler.guiComponents.ChalkBoardPanel;
import com.plancrawler.utilities.MyPoint;

public class GUI extends JFrame {
	// main window which holds the menu, screen area, buttons
	private static final long serialVersionUID = 1L;

	private int width = 1600;
	private int height = (int) Math.floor(width * (9.0 / 16.0));
	private int bottomMargin = 200;
	private int sideMargin = 100;

	// panels:
	private MenuBar menuBar;
	private NavPanel navPanel;
	private ChalkBoardPanel itemCB;
	private Screen centerScreen;

	// support
	private DocumentImages docImages = new DocumentImages();

	// controller
	private TakeOffManager takeOff;
	private String activeItemName = null;

	JLabel pageLabel, activeDetailLabel;

	public GUI() {
		super("PlanCrawler Blueprint Reader");
		this.takeOff = new TakeOffManager();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		setSize(width, height);
	}

	public void init() {
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension dim = tk.getScreenSize();
		if (width > dim.width)
			width = dim.width;
		if (height > dim.height)
			height = dim.height;

		int xpos = (int) (0.5 * (dim.width - width));
		int ypos = (int) (0.5 * (dim.height - height));
		setLocation(xpos, ypos);

		menuBar = new MenuBar();
		this.setJMenuBar(menuBar);

		navPanel = new NavPanel();
		this.add(navPanel, BorderLayout.SOUTH);

		itemCB = new ChalkBoardPanel(dim.width, dim.height);
		this.add(itemCB, BorderLayout.WEST);

		attachCenterScreen();
		setVisible(true);
	}

	public void update() {
		activeItemName = itemCB.getSelectedLine();
		navPanel.update();
		repaint();
	}

	public void showAllMarks() {
		ArrayList<Item> items = takeOff.getItems();
		ArrayList<Mark> showMarks = new ArrayList<Mark>();
		for (Item i : items) {
			ArrayList<Mark> marks = i.getMarks(docImages.getCurrentPage());
			showMarks.addAll(marks);
		}
		centerScreen.displayMarks(showMarks);
	}

	private void attachCenterScreen() {
		MouseHandler mouseHandler = new MouseHandler();
		centerScreen = new Screen(width - 2 * sideMargin, height - bottomMargin);
		centerScreen.addMouseListener(mouseHandler);
		centerScreen.addMouseWheelListener(mouseHandler);
		centerScreen.addMouseMotionListener(mouseHandler);

		this.add(centerScreen, BorderLayout.CENTER);
	}

	private JButton makeButton(String iconFile, String actionName) {
		JButton theButton = new JButton();
		theButton.setText(iconFile);
		// TODO: change this from text to getting an icon
		// Icon buttIcon = new ImageIcon(iconFile);
		// theButton.setIcon(buttIcon);

		theButton.setActionCommand(actionName);
		theButton.addActionListener(new ButtonListener());
		return theButton;
	}

	public String getActiveItemName() {
		return activeItemName;
	}

	public boolean hasActiveItem() {
		return (activeItemName != null);
	}

	private class NavPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		Box buttonBox;
		JSlider pageSlider;
		JLabel pageLabel;

		public NavPanel() {
			buttonBox = Box.createHorizontalBox();
			addComponents();
			this.add(buttonBox, new FlowLayout());
		}

		private void addComponents() {
			JButton focusButt, fitButt, prevButt, nextButt;

			pageSlider = new JSlider();
			pageSlider.setMaximum(docImages.getNumPages());
			pageSlider.setMinimum(1);
			pageSlider.setValue(docImages.getCurrentPage());
			pageSlider.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					if (e.getSource() == pageSlider) {
						int newpage = pageSlider.getValue() - 1;
						changePage(newpage - docImages.getCurrentPage());
						update();
					}
				}
			});

			pageLabel = new JLabel("   Page: " + docImages.getCurrentPage() + " of " + docImages.getNumPages());

			focusButt = makeButton("FOCUS", "FOCUS");
			fitButt = makeButton("FIT", "FIT_TO_SCREEN");

			prevButt = makeNavButton("<|", "PREV_IMAGE");
			nextButt = makeNavButton("|>", "NEXT_IMAGE");

			buttonBox.add(focusButt);
			buttonBox.add(fitButt);
			buttonBox.add(prevButt);
			buttonBox.add(pageLabel);
			buttonBox.add(pageSlider);
			buttonBox.add(nextButt);
		}

		private void resetSlider() {
			int value = Math.min(docImages.getCurrentPage() + 1, docImages.getNumPages());
			pageSlider.setMaximum(docImages.getNumPages());
			pageSlider.setValue(value);
			pageLabel.setText("   Page: " + Integer.toString(value) + " of " + docImages.getNumPages());
		}

		private void update() {
			int value = docImages.getCurrentPage() + 1;
			pageLabel.setText("   Page: " + Integer.toString(value) + " of " + docImages.getNumPages());
		}

		private void changePage(int delta) {
			int page = docImages.getCurrentPage();
			int newPage = page + delta;

			centerScreen.setImage(docImages.getPageImage(newPage));
			pageSlider.setValue(docImages.getCurrentPage() + 1);
			showAllMarks();
		}

		private JButton makeNavButton(String iconFile, String actionName) {
			JButton theButton = new JButton();
			theButton.setText(iconFile);
			// TODO: change this from text to getting an icon
			// Icon buttIcon = new ImageIcon(iconFile);
			// theButton.setIcon(buttIcon);

			theButton.setActionCommand(actionName);
			theButton.addActionListener(new NavButtonListener());
			return theButton;
		}

		private class NavButtonListener implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent e) {
				switch (e.getActionCommand()) {
				case "PREV_IMAGE":
					changePage(-1);
					break;
				case "NEXT_IMAGE":
					changePage(+1);
					break;
				}
			}
		}

	}

	private class MenuBar extends JMenuBar {
		private static final long serialVersionUID = 1L;
		// define the menus
		JMenu fileMenu = new JMenu("File");
		JMenu aboutMenu = new JMenu("About");
		JMenu editMenu = new JMenu("Edit");

		MenuItemListener menuItemListener = new MenuItemListener();

		public MenuBar() {
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

			this.add(fileMenu);
			this.add(editMenu);
			this.add(aboutMenu);
		}

		private class MenuItemListener implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent e) {
				switch (e.getActionCommand()) {
				case "LOAD_PDF":
					docImages.loadPDF(centerScreen);
					centerScreen.setImage(docImages.getCurrentPageImage());
					centerScreen.fitImage();
					navPanel.resetSlider();
					break;
				case "LOAD_IMAGES":
					docImages.loadImages(centerScreen);
					centerScreen.setImage(docImages.getCurrentPageImage());
					navPanel.resetSlider();
					break;
				case "EXIT":
					System.exit(NORMAL);
					break;
				default:
					System.out.println("Didn't code that yet");
				}
				System.out.println(e.getActionCommand() + " menu item clicked!");
			}
		}
	}

	private class ButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			switch (e.getActionCommand()) {
			case "FOCUS":
				centerScreen.focus();
				break;
			case "FIT_TO_SCREEN":
				centerScreen.fitImage();
				break;
			default:
				System.out.println("Not sure what this button does!");
			}
			update();
		}

	}

	private void addMarkToTakeOff(MyPoint screenPt) {
		MyPoint point = centerScreen.getImageRelativePoint(screenPt);
		takeOff.addToItemCount(getActiveItemName(), point, docImages.getCurrentPage());
		takeOff.displayItems();

		// pass info back and forth between takeOff and chalkBoard
		takeOff.batchAddItems(itemCB.getAllItemLines());
		itemCB.setNameToCountMap(takeOff.summaryCount());

		// display marks on the screen
		showAllMarks();
	}

	private class MouseHandler implements MouseWheelListener, MouseInputListener, MouseMotionListener {
		private int mouseX, mouseY;
		private boolean needsFocus = false;
		private boolean isAlreadyOneClick = false;

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			double notches = e.getWheelRotation();
			centerScreen.rescale((1 + notches / 10), e.getX(), e.getY());
			// needsFocus = true;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getSource().equals(centerScreen) && hasActiveItem()) {
				if (isAlreadyOneClick) {
					System.out.println("double click");
					// TODO: add code to open the Item dialog to change item
					// features
					isAlreadyOneClick = false;
				} else {
					isAlreadyOneClick = true;
					Timer t = new Timer("doubleclickTimer", false);
					t.schedule(new TimerTask() {

						@Override
						public void run() {
							// if oneClick is on, then it must have been a
							// single click
							if (isAlreadyOneClick && (e.getButton() == 1)) {
								addMarkToTakeOff(new MyPoint(e.getX(), e.getY()));
							}
							// TODO: add code for removing a mark with a button
							// == 3
							isAlreadyOneClick = false;
						}
					}, 500);
				}
			}
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
