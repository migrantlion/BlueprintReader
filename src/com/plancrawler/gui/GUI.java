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
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
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
import com.plancrawler.iohelpers.WriteToPDF;
import com.plancrawler.measure.LineMark;
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
	private MeasRibbon measRibbon;

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

		toDisplay = new TakeOffDisplay((int) (dim.width / 4.0), (int) (dim.height / 4.0));
		JScrollPane sidePanel = new JScrollPane(toDisplay);

		this.add(sidePanel, BorderLayout.WEST);

		measRibbon = new MeasRibbon(dim);
		this.add(measRibbon, BorderLayout.NORTH);

		attachCenterScreen();
		setVisible(true);
	}

	public synchronized void updateComponents() {
		activeItemName = toDisplay.update();
		activeCrateName = toDisplay.getSelectedCrate();

		navPanel.updateComponents();
		if (navPanel.getRequestedPage() != document.getCurrentPage())
			changePage(navPanel.getRequestedPage());
		
		measRibbon.update();

		// now show the marks
		CopyOnWriteArrayList<Item> items = new CopyOnWriteArrayList<Item>();
		items.addAll(takeOff.getItems());
		CopyOnWriteArrayList<Paintable> showMarks = new CopyOnWriteArrayList<Paintable>();

		// check CB for display status
		for (Item i : items) {
			if (toDisplay.isDisplay(i.getSettings())) {
				CopyOnWriteArrayList<Mark> marks = i.getMarks(document.getCurrentPage());
				showMarks.addAll(marks);
			}
		}

		// add in the rest to display
		showMarks.addAll(takeOff.getNonItemMarks(document.getCurrentPage()));
		if (measRibbon.isMeasuring && measRibbon.getMark() != null)
			showMarks.add(measRibbon.getMark());

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
		else
			takeOff.delMeasure(point, document.getCurrentPage());
	}

	private void addToTakeOff(MyPoint screenPt) {
		MyPoint point = centerScreen.getImageRelativePoint(screenPt);
		if ((activeCrateName != null) && toDisplay.isForPlacement(activeCrateName))
			takeOff.addCrateToTakeOff(activeCrateName, point, document.getCurrentPage());
		else if ((activeCrateName != null) && (activeItemName != null)) {
			warehouse.addItemToCrate(activeCrateName, activeItemName, point, document.getCurrentPage());
			takeOff.update();
		} else if (activeItemName != null)
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

			JMenuItem exportPDF = new JMenuItem("Export PDF");
			exportPDF.setActionCommand("EXPORT_PDF");
			exportPDF.addActionListener(menuItemListener);
			
			JMenuItem exitMenuItem = new JMenuItem("Exit");
			exitMenuItem.setActionCommand("EXIT");
			exitMenuItem.addActionListener(menuItemListener);

			fileMenu.add(loadPDFMenuItem);
			fileMenu.add(loadMenuItem);
			fileMenu.add(saveMenuItem);
			fileMenu.add(exportPDF);
			fileMenu.add(exportImages);
			fileMenu.add(exitMenuItem);

			JMenuItem clearAllMenuItem = new JMenuItem("Clear takeOff");
			clearAllMenuItem.setActionCommand("WIPE");
			clearAllMenuItem.addActionListener(menuItemListener);
			editMenu.add(clearAllMenuItem);

			JMenuItem aboutMenuItem = new JMenuItem("About");
			aboutMenuItem.setActionCommand("ABOUT");
			aboutMenuItem.addActionListener(menuItemListener);
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
				case "EXPORT_PDF":
					WriteToPDF.writeSummaryToPDF(takeOff, document);
					break;
				case "LOAD":
					loadState();
					break;
				case "EXIT":
					System.exit(NORMAL);
					break;
				case "WIPE":
					takeOff.wipe();
					toDisplay.wipe();
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
		MyPoint pt1;

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			double notches = e.getWheelRotation();
			centerScreen.rescale((1 - notches / 10), e.getX(), e.getY());
			needsFocus = true;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (!measRibbon.isMeasuring()) {
				pt1 = null;
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
				} else if (e.getSource().equals(centerScreen) && e.getButton() == 3)
					removeFromTakeOff(new MyPoint(e.getX(), e.getY()));
			} else { // measuring
				if (pt1 == null) {
					MyPoint screenPt = new MyPoint(e.getX(), e.getY());
					pt1 = centerScreen.getImageRelativePoint(screenPt);
					measRibbon.setFirst(pt1);
				} else {
					MyPoint screenPt = new MyPoint(e.getX(), e.getY());
					MyPoint pt2 = centerScreen.getImageRelativePoint(screenPt);
					measRibbon.doMeasurement(pt1, pt2);
					pt1 = null;
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
			measRibbon.setCurrent(centerScreen.getImageRelativePoint(new MyPoint(mouseX, mouseY)));
			if (needsFocus) {
				centerScreen.quickFocus();
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

	private class MeasRibbon extends JPanel {
		private static final long serialVersionUID = 1L;
		private boolean isMeasuring = false;
		JToggleButton measButt;
		JToggleButton calButt;
		MyPoint first, current;
		JLabel isCal = new JLabel("   uncalibrated");
		JComboBox<String> calList;

		public MeasRibbon(int width, int height) {
			this.setSize(width, height);
			this.setLayout(new FlowLayout());
			this.add(setupComponents());
		}

		public MeasRibbon(Dimension dim) {
			this((int) dim.getWidth(), (int) dim.getHeight());
		}
		
		public void update() {
			if (takeOff.isCalibrated(document.getCurrentPage()))
				isCal.setText("   calibrated");
			else
				isCal.setText("   uncalibrated");
		}

		private Box setupComponents() {
			MeasListener measListener = new MeasListener();
			Box ribbon = Box.createHorizontalBox();
			JLabel measureLabel = new JLabel("Measurement Calibration: ");
			calButt = new JToggleButton("[Manual CAL]");
			measButt = new JToggleButton("[Take MEAS]");

			calButt.addActionListener(measListener);
			measButt.addActionListener(measListener);

			ribbon.add(measureLabel);
			ribbon.add(createComboBox());
			ribbon.add(calButt);
			ribbon.add(measButt);
			ribbon.add(isCal);

			return ribbon;
		}
		
		private JComboBox<String> createComboBox() {
			String[] calStrings = { 
					"none",
					"1/4\" = 1ft", 
					"1/3\" = 1ft", 
					"1/2\" = 1ft", 
					"1/8\" = 1ft",
					"custom"};

			//Create the combo box, select item at index 4.
			//Indices start at 0, so 4 specifies the pig.
			calList = new JComboBox<String>(calStrings);
			calList.setEditable(false);
			calList.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (e.getSource()== calList) {
						String choice = (String) calList.getSelectedItem();
						switch (choice) {
						case "1/4\" = 1ft":
							takeOff.calibrate(DocumentHandler.DPI, 1d/4d, 1d, document.getCurrentPage());
							break;
						case "1/3\" = 1ft":
							takeOff.calibrate(DocumentHandler.DPI, 1d/3d, 1d, document.getCurrentPage());
							break;
						case "1/2\" = 1ft":
							takeOff.calibrate(DocumentHandler.DPI, 1d/2d, 1d, document.getCurrentPage());
							break;
						case "1/8\" = 1ft":
							takeOff.calibrate(DocumentHandler.DPI, 1d/8d, 1d, document.getCurrentPage());
							break;
						case "custom":
							calButt.setSelected(true);
							setMeasuring(true);
							break;
						case "none":
							break;
						}
					}
				}
			});
			
			return calList;
		}

		public boolean isMeasuring() {
			return isMeasuring;
		}

		public void setMeasuring(boolean state) {
			isMeasuring = state;
		}

		public void doMeasurement(MyPoint pt1, MyPoint pt2) {
			if (pt1 == null || pt2 == null)
				return;
			
			setMeasuring(false);
			if (calButt.isSelected()) {
				takeOff.calibrate(pt1, pt2, document.getCurrentPage());
				calList.setSelectedIndex(5);
				calButt.setSelected(false);
			} else if (measButt.isSelected()) {
				takeOff.measure(pt1, pt2, document.getCurrentPage());
				measButt.setSelected(false);
			}
			first = null;
			current = null;
		}

		public LineMark getMark() {
			if (isMeasuring && first != null && current != null)
				return new LineMark(first, current);
			else if (isMeasuring && first == null && current != null)
				return new LineMark(current, current);
			else
				return null;
		}

		private class MeasListener implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == calButt) {
					setMeasuring(calButt.isSelected());
					measButt.setSelected(false);
				} else if (e.getSource() == measButt) {
					setMeasuring(measButt.isSelected());
					calButt.setSelected(false);
				}
			}
		}

		public void setFirst(MyPoint first) {
			this.first = first;
		}

		public void setCurrent(MyPoint current) {
			this.current = current;
		}
	}
}
