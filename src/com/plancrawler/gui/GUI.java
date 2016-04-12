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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.MouseInputListener;

import com.plancrawler.elements.DocumentHandler;
import com.plancrawler.elements.Item;
import com.plancrawler.elements.Mark;
import com.plancrawler.elements.TakeOffManager;
import com.plancrawler.guiComponents.ChalkBoardDisplay;
import com.plancrawler.guiComponents.ItemSettingDialog;
import com.plancrawler.guiComponents.NavPanel;
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
	private ChalkBoardDisplay itemCB;
	private Screen centerScreen;

	// support
	private DocumentHandler document;

	// controller
	private TakeOffManager takeOff;
	private String activeItemName = null;

	// private JLabel pageLabel, activeDetailLabel;
	private JLabel pdfNameLabel;

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

		document = new DocumentHandler(this);

		menuBar = new MenuBar();
		this.setJMenuBar(menuBar);

		navPanel = new NavPanel();
		ImageButtPanel imageButtPanel = new ImageButtPanel();

		pdfNameLabel = new JLabel("no file selected");

		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new FlowLayout());
		bottomPanel.add(imageButtPanel);
		bottomPanel.add(navPanel);
		bottomPanel.add(pdfNameLabel);

		this.add(bottomPanel, BorderLayout.SOUTH);
		
		JButton delButt = new JButton("[-]");
		delButt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String removeLine = itemCB.getRemoveLine();
				if (e.getSource().equals(delButt) && removeLine != null) {
					takeOff.delItem(removeLine);
				}
			}
		});

		itemCB = new ChalkBoardDisplay("TakeOff", (int)(dim.width/4.0), (int)(dim.height/4.0), delButt);
		JScrollPane sidePanel = new JScrollPane(itemCB);
		this.add(sidePanel, BorderLayout.WEST);

		attachCenterScreen();
		setVisible(true);
	}

	public synchronized void updateComponents() {
		activeItemName = itemCB.getSelectedLine();
		if (activeItemName != null && !takeOff.hasItemEntry(activeItemName))
			takeOff.makeNewItem(activeItemName);
		
		navPanel.updateComponents();
		if (navPanel.getRequestedPage() != document.getCurrentPage())
			changePage(navPanel.getRequestedPage());
		
		itemCB.updateEntries(takeOff.getItems());
		
		showAllMarks();
		repaint();
	}

	public synchronized void showAllMarks() {
		ArrayList<Item> items = takeOff.getItems();
		ArrayList<Paintable> showMarks = new ArrayList<Paintable>();

		// check CB for display status
		for (Item i : items) {
			if (itemCB.isDisplay(i.getName())) {
				ArrayList<Mark> marks = i.getMarks(document.getCurrentPage());
				showMarks.addAll(marks);
			}
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

	public String getActiveItemName() {
		return activeItemName;
	}

	public boolean hasActiveItem() {
		return (activeItemName != null);
	}

	private void addMarkToTakeOff(MyPoint screenPt) {
		MyPoint point = centerScreen.getImageRelativePoint(screenPt);
		takeOff.addToItemCount(getActiveItemName(), point, document.getCurrentPage());
	}

	private void removeMarkFromTakeOff(MyPoint screenPt) {
		MyPoint point = centerScreen.getImageRelativePoint(screenPt);
		takeOff.subtractItemCount(getActiveItemName(), point, document.getCurrentPage());
	}

	private void changeItemInfo() {
		if (getActiveItemName() != null) {
			Item theItem = takeOff.getItemByName(getActiveItemName());
			if (theItem != null)
				// theItem = takeOff.addNewItem(getActiveItemName());
				// }
				theItem.setSettings(ItemSettingDialog.pickNewSettings(centerScreen, theItem.getSettings()));
		}
	}

	public void changePage(int newPage) {
		// centerScreen.setImage(docImages.getPageImage(newPage));
		// navPanel.setCurrentPage(docImages.getCurrentPage());
		centerScreen.setImage(document.getPageImage(newPage));
		navPanel.setCurrentPage(document.getCurrentPage());
		navPanel.updateComponents();
	}

	private void saveState() {
		JFileChooser chooser = new JFileChooser();
		// chooser.setFileSelectionMode(JFileChooser.SAVE_DIALOG);
		chooser.showSaveDialog(centerScreen);

		String path = chooser.getSelectedFile().getAbsolutePath();
		if (!path.endsWith(".pto"))
			path += ".pto";

		try {
			FileOutputStream fileStream = new FileOutputStream(path);
			ObjectOutputStream os = new ObjectOutputStream(fileStream);

			os.writeObject(takeOff);
			os.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadState() {
		JFileChooser chooser = new JFileChooser();
		chooser.showOpenDialog(centerScreen);

		String path = chooser.getSelectedFile().getAbsolutePath();
		if (!(path.endsWith(".pto") || path.endsWith(".PTO"))) {
			JOptionPane.showMessageDialog(centerScreen, "Must load a .PTO file!", "Incorrect file chosen",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		try {
			FileInputStream fileStream = new FileInputStream(path);
			ObjectInputStream is = new ObjectInputStream(fileStream);

			takeOff = (TakeOffManager) is.readObject();
			is.close();

			// itemCB.reBuildCB(takeOff.summaryCount());
			itemCB.wipeBoard();
			itemCB.updateEntries(takeOff.getItems());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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

			JMenuItem loadMenuItem = new JMenuItem("Load TakeOff");
			loadMenuItem.setActionCommand("LOAD");
			loadMenuItem.addActionListener(menuItemListener);

			JMenuItem saveMenuItem = new JMenuItem("Save TakeOff");
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
			fileMenu.add(loadMenuItem);
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
					takeOff.wipe();
					pdfNameLabel.setText(document.loadPDF());
					centerScreen.setImage(document.getCurrentPageImage());
					navPanel.setNumPages(document.getNumPages());
					navPanel.setCurrentPage(document.getCurrentPage());
					itemCB.clearCounts();
					centerScreen.fitImage();
					break;
				case "LOAD_IMAGES":
					System.out.println("Not supported in this version");
					break;
				case "SAVE":
					saveState();
					break;
				case "LOAD":
					loadState();
					break;
				case "EXIT":
					System.exit(NORMAL);
					break;
				default:
					System.out.println("Didn't code that yet");
				}
			}
		}
	}

	private class MouseHandler implements MouseWheelListener, MouseInputListener, MouseMotionListener {
		private int mouseX, mouseY;
		private boolean needsFocus = false;
		private boolean isAlreadyOneClick = false;

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			double notches = e.getWheelRotation();
			centerScreen.rescale((1 - notches / 10), e.getX(), e.getY());
			// needsFocus = true;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getSource().equals(centerScreen) && hasActiveItem()) {
				if (isAlreadyOneClick) {
					System.out.println("double click");
					if (e.getButton() == 3)
						changeItemInfo();
					isAlreadyOneClick = false;
				} else {
					isAlreadyOneClick = true;
					Timer t = new Timer("doubleclickTimer", false);
					t.schedule(new TimerTask() {

						@Override
						public void run() {
							// if oneClick is on, then it must have been a
							// single click
							if (isAlreadyOneClick) {
								if (e.getButton() == 1)
									addMarkToTakeOff(new MyPoint(e.getX(), e.getY()));
								else if (e.getButton() == 3)
									removeMarkFromTakeOff(new MyPoint(e.getX(), e.getY()));
							}
							isAlreadyOneClick = false;
						}
					}, 500);
				}
			}
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
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

	private class ImageButtPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		private Box box;
		private JButton focusButt, fitButt, rotPlusButt, rotMinusButt, rotAllButt;
		private ImageButtListener imageButtListener;

		ImageButtPanel() {
			addComponents();
			this.add(box, new FlowLayout());
		}

		private void addComponents() {
			imageButtListener = new ImageButtListener();
			box = Box.createHorizontalBox();

			fitButt = new JButton("FIT");
			fitButt.setActionCommand("FIT_TO_SCREEN");
			fitButt.addActionListener(imageButtListener);

			focusButt = new JButton("FOCUS");
			focusButt.setActionCommand("FOCUS");
			focusButt.addActionListener(imageButtListener);

			rotPlusButt = new JButton("ROT+");
			rotPlusButt.setActionCommand("ROT_PLUS");
			rotPlusButt.addActionListener(imageButtListener);

			rotMinusButt = new JButton("ROT-");
			rotMinusButt.setActionCommand("ROT_MINUS");
			rotMinusButt.addActionListener(imageButtListener);

			rotAllButt = new JButton("R ALL");
			rotAllButt.setActionCommand("APPLY_ROT_TO_ALL");
			rotAllButt.addActionListener(imageButtListener);

			box.add(fitButt);
			box.add(focusButt);
			box.add(rotPlusButt);
			box.add(rotMinusButt);
			box.add(rotAllButt);
		}

		private class ImageButtListener implements ActionListener {

			@Override
			public void actionPerformed(ActionEvent e) {
				switch (e.getActionCommand()) {
				case "FOCUS":
					centerScreen.focus();
					break;
				case "FIT_TO_SCREEN":
					centerScreen.fitImage();
					break;
				case "ROT_PLUS":
					document.registerRotation(Math.PI / 2);
					centerScreen.setImage(document.getCurrentPageImage());
					break;
				case "ROT_MINUS":
					document.registerRotation(-Math.PI / 2);
					centerScreen.setImage(document.getCurrentPageImage());
					break;
				case "APPLY_ROT_TO_ALL":
					document.registerRotationToAllPages();
					break;
				default:
					System.out.println("Not sure what this button does!");
				}
			}
		}
	}
}
