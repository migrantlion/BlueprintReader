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
import com.plancrawler.elements.Settings;
import com.plancrawler.elements.TakeOffManager;
import com.plancrawler.guiComponents.NavPanel;
import com.plancrawler.guiComponents.SettingsDialog;
import com.plancrawler.guiComponents.TakeOffDisplay;
import com.plancrawler.iohelpers.PageImageOutput;
import com.plancrawler.utilities.MyPoint;
import com.plancrawler.warehouse.Warehouse;

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
	private TakeOffDisplay toDisplay;
	private Screen centerScreen;

	// support
	private DocumentHandler document;
	private Warehouse warehouse;

	// controller
	private TakeOffManager takeOff;
	private Settings activeItemName = null;
	private Settings activeCrateName = null;

	private JLabel pdfNameLabel;

	public GUI() {
		super("PlanCrawler Blueprint Reader");
		this.takeOff = TakeOffManager.getInstance();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		setSize(width, height);
		warehouse = Warehouse.getInstance();
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
		
		toDisplay = new TakeOffDisplay((int)(dim.width/4.0), (int)(dim.height/4.0));
		JScrollPane sidePanel = new JScrollPane(toDisplay);
		
		this.add(sidePanel, BorderLayout.WEST);

		attachCenterScreen();
		setVisible(true);
	}

	public synchronized void updateComponents() {
//		takeOff.update();
		//TODO:  implement code to keep showroom synched if warehouse changes.
		
		activeItemName = toDisplay.update();
		activeCrateName = toDisplay.getSelectedCrate();
		
		navPanel.updateComponents();
		if (navPanel.getRequestedPage() != document.getCurrentPage())
			changePage(navPanel.getRequestedPage());
		
		// now show the marks
		ArrayList<Item> items = new ArrayList<Item>();
		items.addAll(takeOff.getItems());
		ArrayList<Paintable> showMarks = new ArrayList<Paintable>();

		// check CB for display status
		for (Item i : items) {
			if (toDisplay.isDisplay(i.getSettings())) {
				ArrayList<Mark> marks = i.getMarks(document.getCurrentPage());
				showMarks.addAll(marks);
			}
		}
		
		// add in the crates to display
		showMarks.addAll(takeOff.getShowroomMarks(document.getCurrentPage()));
		showMarks.addAll(takeOff.getWarehouseMarks(document.getCurrentPage()));
		centerScreen.displayMarks(showMarks);
		
		repaint();
	}

	private void attachCenterScreen() {
		MouseHandler mouseHandler = new MouseHandler();
		centerScreen = new Screen(width - 2 * sideMargin, height - bottomMargin);
		centerScreen.addMouseListener(mouseHandler);
		centerScreen.addMouseWheelListener(mouseHandler);
		centerScreen.addMouseMotionListener(mouseHandler);

		this.add(centerScreen, BorderLayout.CENTER);
	}

	public boolean hasActiveItem() {
		return (activeItemName != null || activeCrateName != null);
	}

	private void removeFromTakeOff(MyPoint screenPt) {
		MyPoint point = centerScreen.getImageRelativePoint(screenPt);
		if ((activeCrateName != null) && toDisplay.isForPlacement(activeCrateName))
			takeOff.removeCrateFromTakeOff(activeCrateName, point, document.getCurrentPage());
		else if ((activeCrateName != null) && (activeItemName != null))
			warehouse.delItemFromCrate(activeCrateName, activeItemName, point, document.getCurrentPage());
		else if (activeItemName != null)				
			takeOff.subtractItemCount(activeItemName, point, document.getCurrentPage());

	}
	
	private void addToTakeOff(MyPoint screenPt) {
		MyPoint point = centerScreen.getImageRelativePoint(screenPt);
		if ((activeCrateName != null) && toDisplay.isForPlacement(activeCrateName))
			takeOff.addCrateToTakeOff(activeCrateName, point, document.getCurrentPage());
		else if ((activeCrateName != null) && (activeItemName != null))	{	
			warehouse.addItemToCrate(activeCrateName, activeItemName, point, document.getCurrentPage());
			takeOff.update();
		}
		else if (activeItemName != null)
			takeOff.addToItemCount(activeItemName, point, document.getCurrentPage());
	}
	
	private void changeItemInfo() {
		if (activeItemName != null) {
			Item item = takeOff.getItemBySetting(activeItemName);
			if (item != null) {
				item.setSettings(SettingsDialog.pickNewSettings(centerScreen, item.getSettings()));
				takeOff.setChanged(true);
			}
		}
	}

	public void changePage(int newPage) {
		centerScreen.setImage(document.getPageImage(newPage));
		navPanel.setCurrentPage(document.getCurrentPage());
		navPanel.updateComponents();
	}

	private synchronized void saveState() {
		String defaultDir = "C:\\Users\\Steve.Soss\\Documents\\PlanCrawler\\Saved TakeOffs\\";
		JFileChooser chooser = new JFileChooser(defaultDir);
		chooser.showSaveDialog(centerScreen);

		String path = chooser.getSelectedFile().getAbsolutePath();
		if (!path.endsWith(".pto"))
			path += ".pto";

		try {
			FileOutputStream fileStream = new FileOutputStream(path);
			ObjectOutputStream os = new ObjectOutputStream(fileStream);

			os.writeObject(takeOff);
			os.writeObject(document);
			os.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private synchronized void loadState() {
		String defaultDir = "C:\\Users\\Steve.Soss\\Documents\\PlanCrawler\\Saved TakeOffs\\";
		JFileChooser chooser = new JFileChooser(defaultDir);
		int choice = chooser.showOpenDialog(centerScreen);
		
		if (choice != JFileChooser.APPROVE_OPTION)
			return;
		
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
			document = (DocumentHandler) is.readObject();
			is.close();

			// reset the singleton references
			warehouse = takeOff.getWarehouse();
			toDisplay.reAttachSupports(takeOff, warehouse);
				
			takeOff.setChanged(true);
			document.setCurrentFile(takeOff.getPDFName());
			pdfNameLabel.setText(takeOff.getPDFName());
			centerScreen.setImage(document.getCurrentPageImage());
			navPanel.setNumPages(document.getNumPages());
			navPanel.setCurrentPage(document.getCurrentPage());
			centerScreen.fitImage();
			toDisplay.setForceUpdate(true);
			toDisplay.update();

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

			JMenuItem loadMenuItem = new JMenuItem("Load TakeOff");
			loadMenuItem.setActionCommand("LOAD");
			loadMenuItem.addActionListener(menuItemListener);

			JMenuItem saveMenuItem = new JMenuItem("Save TakeOff");
			saveMenuItem.setActionCommand("SAVE");
			saveMenuItem.addActionListener(menuItemListener);

			JMenuItem exportImages = new JMenuItem("Export Images");
			exportImages.setActionCommand("EXPORT_IMAGES");
			exportImages.addActionListener(menuItemListener);
			
			JMenuItem exitMenuItem = new JMenuItem("Exit");
			exitMenuItem.setActionCommand("EXIT");
			exitMenuItem.addActionListener(menuItemListener);

			JMenuItem aboutMenuItem = new JMenuItem("About");
			aboutMenuItem.setActionCommand("ABOUT");
			aboutMenuItem.addActionListener(menuItemListener);

			fileMenu.add(loadPDFMenuItem);
			fileMenu.add(loadMenuItem);
			fileMenu.add(saveMenuItem);
			fileMenu.add(exportImages);
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
					toDisplay.wipe();
					String pdfName = document.loadPDF();
					pdfNameLabel.setText(pdfName);
					takeOff.setPDFName(pdfName);
					centerScreen.setImage(document.getCurrentPageImage());
					navPanel.setNumPages(document.getNumPages());
					navPanel.setCurrentPage(document.getCurrentPage());
					centerScreen.fitImage();
					break;
				case "SAVE":
					saveState();
					break;
				case "EXPORT_IMAGES":
					PageImageOutput.writePagesWithMarks(takeOff, document);
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
									addToTakeOff(new MyPoint(e.getX(), e.getY()));
								else if (e.getButton() == 3)
									removeFromTakeOff(new MyPoint(e.getX(), e.getY()));
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
