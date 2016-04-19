package com.plancrawler.guiComponents;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import com.plancrawler.elements.Item;
import com.plancrawler.elements.Settings;
import com.plancrawler.elements.TakeOffManager;
import com.plancrawler.warehouse.Crate;
import com.plancrawler.warehouse.Warehouse;

public class TakeOffDisplay extends JPanel {
	/*
	 * Displays the contents of the TakeOff. Items will always be in sort order
	 * Which is sorted by category, then by name. PassBack is the selectedLine,
	 * and the display feature.
	 */

	private static final long serialVersionUID = 1L;

	private ArrayList<CBEntry> entries;
	private ArrayList<WHEntry> whEntries;

	private JPanel board, house;
	private Settings selectedLine = null;
	private Settings selectedCrate = null;
	private TakeOffManager takeOff;
	private Warehouse warehouse;
	private double timer = 0;

	public TakeOffDisplay(int width, int height) {
		this.setSize(width, height);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		entries = new ArrayList<CBEntry>();
		whEntries = new ArrayList<WHEntry>();

		prepBoard();
		this.add(board);

		// this section for the warehouse
		prepHouse();
		// makeWHButts();
		this.add(house);

		this.takeOff = TakeOffManager.getInstance();
		this.warehouse = Warehouse.getInstance();
	}

	public synchronized void wipeBoard() {
		eraseBoard();
		// remove all entries
		entries = new ArrayList<CBEntry>();
	}

	public void prepBoard() {
		board = new JPanel();
		board.setSize(this.getSize());
		board.setLayout(new BoxLayout(board, BoxLayout.Y_AXIS));
		board.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

		Box mainbox = Box.createVerticalBox();

		Box topbox = Box.createHorizontalBox();
		ItemEntryDisplay ieDisplay = new ItemEntryDisplay(this.getSize(), "board");
		topbox.add(ieDisplay);
		mainbox.add(topbox);

		Box box = Box.createHorizontalBox();
		JLabel separator = new JLabel("------items-----");
		separator.setHorizontalAlignment(JLabel.CENTER);
		box.add(separator);

		mainbox.add(box);
		board.add(mainbox);

		// fulldisplay.add(board);
		// fulldisplay.add(whDisplay);
	}

	public void prepHouse() {
		house = new JPanel();
		house.setSize(this.getSize());
		house.setLayout(new BoxLayout(house, BoxLayout.Y_AXIS));
		house.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

		Box mainbox = Box.createVerticalBox();

		Box topbox = Box.createHorizontalBox();
		ItemEntryDisplay ieDisplay = new ItemEntryDisplay(this.getSize(), "house");
		topbox.add(ieDisplay);
		mainbox.add(topbox);

		Box box = Box.createHorizontalBox();
		JLabel separator = new JLabel("------crates-----");
		separator.setHorizontalAlignment(JLabel.CENTER);
		box.add(separator);

		mainbox.add(box);
		house.add(mainbox);
	}

//	private void makeWHButts() {
//		house = new JPanel();
//		house.setSize(this.getSize());
//		house.setLayout(new BoxLayout(house, BoxLayout.Y_AXIS));
//		house.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
//
//		Box topbox = Box.createHorizontalBox();
//		JLabel whLabel1 = new JLabel("------------------");
//		JLabel whLabel = new JLabel("warehouse:     ");
//		topbox.add(whLabel);
//		topbox.add(whLabel1);
//
//		Box botbox = Box.createHorizontalBox();
//		JLabel cratesLabel = new JLabel("crates:");
//		JTextField crateEntry = new JTextField(10);
//		crateEntry.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				if (e.getSource() == crateEntry) {
//					String cname = crateEntry.getText();
//					if (!warehouse.crateInWareHouse(cname)) {
//						warehouse.adddNewCrate(cname);
//						warehouse.setChanged(true);
//					}
//					crateEntry.setText(null);
//				}
//			}
//		});
//		botbox.add(cratesLabel);
//		JButton crateButt = new JButton("[+]");
//		crateButt.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				if (e.getSource() == crateButt) {
//					Settings crateSetting = SettingsDialog.pickNewSettings(board, null);
//					if (!warehouse.crateInWareHouse(crateSetting.getName()))
//						warehouse.adddNewCrate(crateSetting);
//					warehouse.setChanged(true);
//				}
//			}
//		});
//		botbox.add(crateEntry);
//		botbox.add(crateButt);
//
//		Box box = Box.createHorizontalBox();
//		JLabel separator = new JLabel("------crates-----");
//		separator.setHorizontalAlignment(JLabel.CENTER);
//		box.add(separator);
//
//		house.add(topbox);
//		house.add(botbox);
//		house.add(box);
//	}

	public Settings getSelectedLine() {
		return selectedLine;
	}

	public Settings getSelectedCrate() {
		return selectedCrate;
	}

	public void clearCounts() {
		for (CBEntry e : entries)
			e.setQuant(0);
	}

	private boolean isOnBoard(Settings name) {
		boolean answer = false;
		for (CBEntry e : entries) {
			if (e.getSettings().equals(name))
				answer = true;
		}
		return answer;
	}

	private boolean isCrateOnBoard(Settings name) {
		boolean answer = false;
		for (WHEntry e : whEntries) {
			if (e.getSettings().equals(name))
				answer = true;
		}
		return answer;
	}

	// private boolean isCrateOnBoard(String name) {
	// boolean answer = false;
	// for (WHEntry wh : whEntries)
	// if (wh.getSettings().getName().equals(name))
	// answer = true;
	// return answer;
	// }

	private void changeItemInfo() {
		if (selectedLine != null) {
			Item item = takeOff.getItemBySetting(selectedLine);
			if (item != null) {
				item.setSettings(SettingsDialog.pickNewSettings(board, item.getSettings()));
				takeOff.setChanged(true);
			}
		}
	}

	private void changeCrateInfo() {
		if (selectedCrate != null) {
			Crate crate = warehouse.getCrate(selectedCrate);
			if (crate != null) {
				crate.putSettings(SettingsDialog.pickNewSettings(board, crate.getSettings()));
				warehouse.setChanged(true);
			}
		}
	}

	private synchronized void eraseBoard() {
		// detach all the entries from the board
		for (CBEntry e : entries)
			board.remove(e.coverBox);

		for (WHEntry e : whEntries)
			house.remove(e.coverBox);
	}

	private void sortEntries() {
		// detach all the JLabel boxes from the board and then re-Attach in alph
		// order by category, then by item name
		eraseBoard();

		// sort the array list
		Collections.sort(entries);
		Collections.sort(whEntries);

		// re-attach the boxes
		for (CBEntry e : entries)
			board.add(e.coverBox);
		for (WHEntry e : whEntries)
			house.add(e.coverBox);

		validate();
		repaint();
	}

	public Settings update() {
		if (takeOff.hasChanged() || timer > 1000) {
			updateCBEntries(takeOff.getItems());
			updateCBEntries(takeOff.getShowroomItems());
			timer = 0;
		}

		if (warehouse.hasChanged()) {
			updateWHEntries(warehouse.getInventory());
		}

		validate();
		repaint();

		return getSelectedLine();
	}

	private synchronized void updateWHEntries(ArrayList<Crate> info) {
		for (Crate c : info) {
			if (!isCrateOnBoard(c.getSettings()))
				addWHEntry(c.getSettings());
			if (getWHEntry(c.getSettings()) != null)
				getWHEntry(c.getSettings()).update(c.getSettings(), c.rawCount());
		}

		sortEntries();

		// remove those which are not in warehouse anymore
		ArrayList<WHEntry> orphans = new ArrayList<WHEntry>();
		for (WHEntry we : whEntries) {
			boolean isOrphan = true;
			for (Crate c : info)
				if (c.getSettings().equals(we.getSettings()))
					isOrphan = false;
			if (isOrphan)
				orphans.add(we);
		}
		for (WHEntry we : orphans)
			delEntry(we);

		validate();
		repaint();
	}

	private synchronized void updateCBEntries(HashMap<Settings, Integer> info) {
		for (Settings s : info.keySet()) {
			if (!isOnBoard(s))
				addEntry(s);
			if (getCBEntry(s) != null)
				getCBEntry(s).update(s, info.get(s) + getCBEntry(s).getQuant());
		}
		sortEntries();

		validate();
		repaint();
	}

	private synchronized void updateCBEntries(ArrayList<Item> info) {
		for (Item i : info) {
			if (!isOnBoard(i.getSettings()))
				addEntry(i.getSettings());
			if (getCBEntry(i.getSettings()) != null)
				getCBEntry(i.getSettings()).update(i.getSettings(), i.count());
		}
		sortEntries();

		// remove those items which are not in the info list
		ArrayList<CBEntry> orphans = new ArrayList<CBEntry>();
		for (CBEntry cb : entries) {
			boolean isOrphan = true;
			for (Item i : info)
				if (i.getSettings().equals(cb.getSettings()))
					isOrphan = false;
			if (isOrphan)
				orphans.add(cb);
		}
		for (CBEntry cb : orphans) {
			delEntry(cb);
		}

		validate();
		repaint();
	}

	private synchronized CBEntry getCBEntry(Settings itemSettings) {
		CBEntry entry = null;
		for (CBEntry e : entries)
			if (e.getSettings().equals(itemSettings))
				entry = e;
		return entry;
	}

	private synchronized WHEntry getWHEntry(Settings settings) {
		WHEntry entry = null;
		for (WHEntry e : whEntries)
			if (e.getSettings().equals(settings))
				entry = e;
		return entry;
	}

	private synchronized void addEntry(Settings itemSettings) {
		if (!isOnBoard(itemSettings)) {
			CBEntry entry = new CBEntry(itemSettings);
			entries.add(entry);
			board.add(entry.coverBox);
		}
	}

	private void addWHEntry(Settings crateSettings) {
		if (!isCrateOnBoard(crateSettings)) {
			WHEntry entry = new WHEntry(crateSettings);
			whEntries.add(entry);
			house.add(entry.coverBox);
			validate();
			repaint();
		}
	}

	private synchronized void delEntry(CBEntry cb) {
		board.remove(cb.coverBox);
		entries.remove(cb);
	}

	private synchronized void delEntry(WHEntry cb) {
		board.remove(cb.coverBox);
		entries.remove(cb);
	}

	public void deselectAllLabels() {
		for (CBEntry e : entries) {
			e.plain();
		}
		selectedLine = null;

		validate();
		repaint();
	}

	public void deselectAllCrates() {
		for (WHEntry e : whEntries) {
			e.plain();
		}
		selectedCrate = null;

		validate();
		repaint();
	}

	public boolean isDisplay(Settings itemSettings) {
		if (isOnBoard(itemSettings)) {
			return getCBEntry(itemSettings).isDisplay();
		} else {
			// System.out.println("No item " + itemSettings.getName() + " found
			// in TakeOffDisplay");
			return false;
		}
	}

	public boolean isForPlacement(Settings crateInfo) {
		if (isCrateOnBoard(crateInfo)) {
			return getWHEntry(crateInfo).isForPlacement();
		} else
			return false;
	}

	private class CBEntry extends MouseAdapter implements Comparable<CBEntry> {
		private Settings settings;
		private JLabel itemName;
		private JLabel itemQuant;
		private JTextField itemDesc;
		private JTextField itemCat;
		private JButton colorButt;
		private JRadioButton displayButt;
		private JPanel coverBox;
		private int quant = 0;

		public CBEntry(Settings settings) {
			this.settings = settings;
			setupItems();
			highlight();
		}

		public void update(Settings settings, int count) {
			this.settings = settings;
			this.quant = count;
			setName(settings.getName());
			setCategory(settings.getCategory());
			setDesc(settings.getDescription());
			setQuant(count);
			setColor(settings.getColor());
		}

		public int getQuant() {
			return quant;
		}

		public Settings getSettings() {
			return settings;
		}

		public boolean isDisplay() {
			return displayButt.isSelected();
		}

		public void highlight() {
			deselectAllLabels();
			selectedLine = settings;
			itemName.setForeground(Color.blue);
			coverBox.setBorder(BorderFactory.createLineBorder(Color.blue, 3));
		}

		public void plain() {
			itemName.setForeground(Color.black);
			coverBox.setBorder(null);
		}

		private synchronized void setupItems() {
			coverBox = new JPanel();
			coverBox.setLayout(new BoxLayout(coverBox, BoxLayout.Y_AXIS));
			coverBox.setName(settings.getName());
			coverBox.addMouseListener(this);

			itemName = new JLabel(settings.getName());
			itemName.setHorizontalAlignment(JLabel.CENTER);

			itemQuant = new JLabel("0");
			itemQuant.setHorizontalAlignment(JLabel.CENTER);

			itemDesc = new JTextField(settings.getDescription(), 12);
			itemDesc.setHorizontalAlignment(JLabel.CENTER);
			JScrollPane descScroll = new JScrollPane(itemDesc);

			itemCat = new JTextField(settings.getCategory() + ": ", 12);
			itemCat.setHorizontalAlignment(JLabel.LEFT);
			JScrollPane catScroll = new JScrollPane(itemCat);

			colorButt = new JButton(".");
			colorButt.setBackground(settings.getColor());
			colorButt.setForeground(settings.getColor());
			colorButt.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (e.getSource() == colorButt) {
						highlight();
						changeItemInfo();
					}
				}
			});

			displayButt = new JRadioButton("on", true);
			displayButt.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (e.getSource() == displayButt) {
						JRadioButton rb = (JRadioButton) e.getSource();
						if (rb.isSelected())
							rb.setText("on");
						else
							rb.setText("off");
					}
				}
			});

			JPanel topLine = new JPanel();
			topLine.setLayout(new GridLayout());
			JPanel botLine = new JPanel();
			botLine.setLayout(new FlowLayout());

			topLine.add(displayButt);
			topLine.add(colorButt);
			topLine.add(itemName);
			topLine.add(itemQuant);
			botLine.add(catScroll);
			botLine.add(descScroll);

			coverBox.add(topLine);
			coverBox.add(botLine);
		}

		public void setName(String name) {
			itemName.setText(name);
		}

		public void setCategory(String category) {
			itemCat.setText(category + ": ");
		}

		public void setDesc(String desc) {
			itemDesc.setText(desc);
		}

		public void setQuant(int count) {
			itemQuant.setText(Integer.toString(count));
		}

		public void setColor(Color color) {
			colorButt.setBackground(color);
			colorButt.setForeground(color);
		}

		public void mouseClicked(MouseEvent e) {
			if (settings.equals(selectedLine)) {
				deselectAllLabels();
			} else {
				highlight();
			}
		}

		@Override
		public int compareTo(CBEntry other) {
			return this.settings.compareTo(other.settings);
		}
	}

	private class WHEntry extends MouseAdapter implements Comparable<WHEntry> {
		private Settings settings;
		private JLabel crateName;
		private JLabel crateQuant;
		private JLabel crateDesc;
		private JLabel crateCat;
		private JButton colorButt;
		private JRadioButton displayButt;
		private JPanel coverBox;

		public WHEntry(Settings settings) {
			this.settings = settings;
			setupItems();
		}

		public void update(Settings settings, int count) {
			this.settings = settings;
			setName(settings.getName());
			setCategory(settings.getCategory());
			setDesc(settings.getDescription());
			setQuant(count);
			setColor(settings.getColor());
		}

		public Settings getSettings() {
			return settings;
		}

		public boolean isForPlacement() {
			return displayButt.isSelected();
		}

		public void highlight() {
			deselectAllCrates();
			selectedCrate = settings;
			crateName.setForeground(Color.blue);
			coverBox.setBorder(BorderFactory.createLineBorder(Color.blue, 3));
		}

		public void plain() {
			crateName.setForeground(Color.black);
			coverBox.setBorder(null);
		}

		private synchronized void setupItems() {
			coverBox = new JPanel();
			coverBox.setLayout(new BoxLayout(coverBox, BoxLayout.Y_AXIS));
			coverBox.setName(settings.getName());
			coverBox.addMouseListener(this);

			crateName = new JLabel(settings.getName());
			crateName.setHorizontalAlignment(JLabel.CENTER);

			crateQuant = new JLabel("0");
			crateQuant.setHorizontalAlignment(JLabel.CENTER);

			crateDesc = new JLabel(settings.getDescription(), JLabel.RIGHT);
			crateDesc.setHorizontalAlignment(JLabel.CENTER);
			JScrollPane descScroll = new JScrollPane(crateDesc);

			crateCat = new JLabel(settings.getCategory() + ": ", JLabel.LEFT);
			crateCat.setHorizontalAlignment(JLabel.LEFT);
			JScrollPane catScroll = new JScrollPane(crateCat);

			colorButt = new JButton(".");
			colorButt.setBackground(settings.getColor());
			colorButt.setForeground(settings.getColor());
			colorButt.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (e.getSource() == colorButt) {
						highlight();
						changeCrateInfo();
					}
				}
			});

			displayButt = new JRadioButton("place off", false);
			displayButt.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (e.getSource() == displayButt) {
						JRadioButton rb = (JRadioButton) e.getSource();
						if (rb.isSelected())
							rb.setText("place on ");
						else
							rb.setText("place off");
					}
				}
			});

			JPanel topLine = new JPanel();
			topLine.setLayout(new GridLayout());
			JPanel botLine = new JPanel();
			botLine.setLayout(new FlowLayout());

			topLine.add(displayButt);
			topLine.add(colorButt);
			topLine.add(crateName);
			topLine.add(crateQuant);
			botLine.add(catScroll);
			botLine.add(descScroll);

			coverBox.add(topLine);
			coverBox.add(botLine);
		}

		public void setName(String name) {
			crateName.setText(name);
		}

		public void setCategory(String category) {
			crateCat.setText(category + ": ");
		}

		public void setDesc(String desc) {
			crateDesc.setText(desc);
		}

		public void setQuant(int count) {
			crateQuant.setText(Integer.toString(count));
		}

		public void setColor(Color color) {
			colorButt.setBackground(color);
			colorButt.setForeground(color);
		}

		public void mouseClicked(MouseEvent e) {
			if (settings.equals(selectedCrate)) {
				deselectAllCrates();
			} else {
				highlight();
			}
		}

		@Override
		public int compareTo(WHEntry other) {
			return this.settings.compareTo(other.settings);
		}
	}

	private class ItemEntryDisplay extends JPanel {
		private static final long serialVersionUID = 1L;
		private JLabel entryLabel;
		private JTextField itemEntry;
		private JPanel board;
		private JButton delButt;
		private IEActionListener listener = new IEActionListener();
		private String type;

		public ItemEntryDisplay(int width, int height, String type) {
			this.setSize(width, 100);
			this.setLayout(new FlowLayout());
			this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
			this.type = type;
			prepBoard();
			this.add(board);
		}

		public ItemEntryDisplay(Dimension size, String type) {
			this((int) size.getWidth(), (int) size.getHeight(), type);
		}

		private void prepBoard() {
			board = new JPanel();
			board.setSize(this.getSize());
			board.setLayout(new BoxLayout(board, BoxLayout.Y_AXIS));

			Box box = Box.createVerticalBox();
			JLabel titleLabel = new JLabel();
			if (type.equals("board"))
				titleLabel.setText("TakeOff");
			else
				titleLabel.setText("Warehouse");
			titleLabel.setHorizontalAlignment(JLabel.CENTER);

			Box topBox = Box.createHorizontalBox();

			entryLabel = new JLabel("new item: ");
			if (type.equals("house"))
				entryLabel.setText("new crate: ");

			topBox.add(entryLabel);
			itemEntry = new JTextField(15);
			itemEntry.addActionListener(listener);
			topBox.add(itemEntry);

			delButt = new JButton("[DEL]");
			delButt.addActionListener(listener);
			topBox.add(delButt);

			box.add(titleLabel);
			box.add(new JLabel("  ")); // blank line
			box.add(topBox);

			board.add(box);
		}

		private void addEntry(String name) {
			if (type.equals("board")) {
				if (!takeOff.hasItemName(name))
					takeOff.addNewItem(new Item(new Settings(name)));
			} else if (type.equals("house"))
				if (!warehouse.crateInWareHouse(name))
					warehouse.adddNewCrate(name);
		}

		private void removeSelectedLine() {
			if (selectedLine == null)
				return;
			else {
				takeOff.delItemBySetting(selectedLine);
			}
		}
		
		private void removeSelectedCrate() {
			if (selectedCrate == null)
				return;
			else
				warehouse.delCrateBySetting(selectedCrate);
		}

		private class IEActionListener implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource().equals(itemEntry)) {
					String iName = itemEntry.getText();
					itemEntry.setText(null);
					addEntry(iName);
				} else if (e.getSource().equals(delButt)) {
					if (type.equals("board"))
						removeSelectedLine();
					else
						removeSelectedCrate();
				}
			}
		}
	}

}
