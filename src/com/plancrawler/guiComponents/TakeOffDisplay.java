package com.plancrawler.guiComponents;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
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

	private CopyOnWriteArrayList<CBEntry> entries;
	private CopyOnWriteArrayList<WHEntry> whEntries;

	private JPanel board, house;
	private Settings selectedLine = null;
	private Settings selectedCrate = null;
	private TakeOffManager takeOff;
	private Warehouse warehouse;
	private boolean forceUpdate = false;
	private JRadioButton showDesc;

	public TakeOffDisplay(int width, int height) {
		this.setSize(width, height);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		entries = new CopyOnWriteArrayList<CBEntry>();
		whEntries = new CopyOnWriteArrayList<WHEntry>();

		prepBoard();
		this.add(board);

		// this section for the warehouse
		prepHouse();
		// makeWHButts();
		this.add(house);

		this.takeOff = TakeOffManager.getInstance();
		this.warehouse = Warehouse.getInstance();
	}

	public void setForceUpdate(boolean forceUpdate) {
		this.forceUpdate = forceUpdate;
	}
	
	public void reAttachSupports(TakeOffManager takeoff, Warehouse warehouse) {
		this.takeOff = takeoff;
		this.warehouse = warehouse;
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
		showDesc = new JRadioButton("show desc", false);
		showDesc.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource()==showDesc)
					forceUpdate = true;
			}
		});
		JButton dispAll = new JButton("disp all");
		dispAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource()==dispAll)
					setAllDisplay(true);
			}
		});
		box.add(dispAll);
		JButton dispNone = new JButton("disp none");
		dispNone.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource()==dispNone)
					setAllDisplay(false);
			}
		});
		box.add(dispNone);
		
		box.add(showDesc);
		
		JLabel separator = new JLabel("------items-----");
		separator.setHorizontalAlignment(JLabel.CENTER);
		box.add(separator);

		mainbox.add(box);
		board.add(mainbox);
	}

	private void setAllDisplay(boolean state) {
		for (CBEntry cbe : entries) {
			cbe.displayButt.setSelected(state);
			if (state)
				cbe.displayButt.setText("on");
			else
				cbe.displayButt.setText("off");
		}
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

	public void wipe() {
		eraseBoard();
		entries.clear();
		whEntries.clear();
		validate();
		repaint();
	}
	
	public Settings update() {
		if (warehouse.hasChanged() || forceUpdate) {
			updateWHEntries(warehouse.getInventory());
			forceUpdate = true;
		}

		if (takeOff.hasChanged() || forceUpdate) {
			updateCBEntries(takeOff.getItems());
			updateCBEntries(takeOff.getShowroomItems());
			forceUpdate = false;
		}

		validate();
		repaint();

		return getSelectedLine();
	}

	private synchronized void updateWHEntries(CopyOnWriteArrayList<Crate> info) {
		for (Crate c : info) {
			if (!isCrateOnBoard(c.getSettings()))
				addWHEntry(c.getSettings());
			if (getWHEntry(c.getSettings()) != null)
				getWHEntry(c.getSettings()).update(c.getSettings(), c.rawCount());
		}

		sortEntries();

		// remove those which are not in warehouse anymore
		CopyOnWriteArrayList<WHEntry> orphans = new CopyOnWriteArrayList<WHEntry>();
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

	private synchronized void updateCBEntries(CopyOnWriteArrayList<Item> info) {
		for (Item i : info) {
			if (!isOnBoard(i.getSettings()))
				addEntry(i.getSettings());
			if (getCBEntry(i.getSettings()) != null)
				getCBEntry(i.getSettings()).update(i.getSettings(), i.count());
		}
		sortEntries();

		// remove those items which are not in the info list
		CopyOnWriteArrayList<CBEntry> orphans = new CopyOnWriteArrayList<CBEntry>();
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
		private JLabel itemDesc;
		private JLabel itemCat;
		private JButton colorButt;
		private JRadioButton displayButt;
		private JPanel coverBox;
		JPanel botLine = new JPanel();
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
			if (!showDesc.isSelected() && !settings.equals(selectedLine)) {
				coverBox.remove(botLine);
			} else if (showDesc.isSelected()){
				coverBox.add(botLine);
			}
			validate();
			repaint();
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
			coverBox.add(botLine);
		}

		public void plain() {
			itemName.setForeground(Color.black);
			coverBox.setBorder(null);
			if (!showDesc.isSelected()) 
				coverBox.remove(botLine);
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

			itemDesc = new JLabel(settings.getDescription());
			itemDesc.setHorizontalAlignment(JLabel.CENTER);
			
			itemCat = new JLabel(settings.getCategory() + ": ");
			itemCat.setHorizontalAlignment(JLabel.LEFT);
			
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
			topLine.setLayout(new GridLayout(1,0));

			botLine.setLayout(new FlowLayout());

			JPanel groupBox = new JPanel();
			groupBox.setLayout(new GridLayout(1,0));
			groupBox.add(displayButt);
			groupBox.add(colorButt);
			groupBox.add(itemQuant);
			topLine.add(groupBox);
//
//			topLine.add(displayButt);
//			topLine.add(colorButt);
			topLine.add(itemName);
//			topLine.add(itemQuant);
			botLine.add(itemCat);
			botLine.add(itemDesc);

			coverBox.add(topLine);
			if (showDesc.isSelected())
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

			crateCat = new JLabel(settings.getCategory() + ": ", JLabel.LEFT);
			crateCat.setHorizontalAlignment(JLabel.LEFT);

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
			topLine.setLayout(new GridLayout(1,0));
			JPanel botLine = new JPanel();
			botLine.setLayout(new FlowLayout());

			JPanel groupBox = new JPanel();
			groupBox.setLayout(new GridLayout(1,0));
			groupBox.add(displayButt);
			groupBox.add(colorButt);
			groupBox.add(crateQuant);
			topLine.add(groupBox);
			topLine.add(crateName);
			botLine.add(crateCat);
			botLine.add(crateDesc);

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
