package com.plancrawler.guiComponents;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.BevelBorder;

import com.plancrawler.elements.Item;
import com.plancrawler.elements.ItemSettings;

public class TakeOffDisplay extends JPanel {
	/*
	 * Displays the contents of the TakeOff. Items will always be in sort order
	 * Which is sorted by category, then by name. PassBack is the selectedLine,
	 * and the display feature.
	 */

	private static final long serialVersionUID = 1L;

	private String title;
	private ArrayList<CBEntry> entries;

	private JPanel board;
	JLabel titleLabel;
	private ItemSettings selectedLine;
	private boolean requestChange = false;

	public TakeOffDisplay(String title, int width, int height) {
		this.title = title;
		this.setSize(width, height);
		this.setLayout(new FlowLayout());
		this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		entries = new ArrayList<CBEntry>();
		prepBoard();
		this.add(board);
	}

	public synchronized void wipeBoard() {
		eraseBoard();
		// remove all entries
		entries = new ArrayList<CBEntry>();
	}

	public void setTitle(String title) {
		this.title = title;
		titleLabel.setText(title);
	}
	
	public void prepBoard() {
		board = new JPanel();
		board.setSize(this.getSize());
		board.setLayout(new BoxLayout(board, BoxLayout.Y_AXIS));

		Box box = Box.createVerticalBox();
		titleLabel = new JLabel(title);
		titleLabel.setHorizontalAlignment(JLabel.CENTER);

		box.add(titleLabel);
		box.add(new JLabel("  ")); // blank line

		board.add(box);

		Box box2 = Box.createHorizontalBox();
		JLabel separator = new JLabel("------items-----");
		separator.setHorizontalAlignment(JLabel.CENTER);

		box2.add(separator);

		board.add(box2);
	}

	public ItemSettings getSelectedLine() {
		return selectedLine;
	}

	public void clearCounts() {
		for (CBEntry e : entries)
			e.setQuant(0);
	}

	private boolean isOnBoard(ItemSettings name) {
		boolean answer = false;
		for (CBEntry e : entries) {
			if (e.getSettings().equals(name))
				answer = true;
		}
		return answer;
	}

	private synchronized void eraseBoard() {
		// detach all the entries from the board
		for (CBEntry e : entries)
			board.remove(e.coverBox);
	}

	private void sortEntries() {
		// detach all the JLabel boxes from the board and then re-Attach in alph
		// order by category, then by item name
		eraseBoard();

		// sort the array list
		Collections.sort(entries);

		// re-attach the boxes
		for (CBEntry e : entries)
			board.add(e.coverBox);

		validate();
		repaint();
	}

	public synchronized void updateEntries(ArrayList<Item> info) {
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
	}

	private synchronized CBEntry getCBEntry(ItemSettings itemSettings) {
		CBEntry entry = null;
		for (CBEntry e : entries)
			if (e.getSettings().equals(itemSettings))
				entry = e;
		return entry;
	}

	private synchronized void addEntry(ItemSettings itemSettings) {
		if (!isOnBoard(itemSettings)) {
			CBEntry entry = new CBEntry(itemSettings);
			entries.add(entry);
			board.add(entry.coverBox);
		}
	}
	
	private synchronized void delEntry(CBEntry cb) {
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

	public boolean isDisplay(ItemSettings itemSettings) {
		if (isOnBoard(itemSettings)) {
			return getCBEntry(itemSettings).isDisplay();
		} else {
			System.out.println("No item " + itemSettings.getName() + " found in TakeOffDisplay");
			return false;
		}
	}
	
	public boolean isRequestChange() {
		boolean state = requestChange;
		requestChange = false;
		return state;
	}
	
	private class CBEntry extends MouseAdapter implements Comparable<CBEntry> {
		private ItemSettings settings;
		private JLabel itemName;
		private JLabel itemQuant;
		private JLabel itemDesc;
		private JLabel itemCat;
		private JButton colorButt;
		private JRadioButton displayButt;
		private JPanel coverBox;

		public CBEntry(ItemSettings settings) {
			this.settings = settings;
			setupItems();
			highlight();
		}

		public void update(ItemSettings settings, int count) {
			this.settings = settings;
			setCategory(settings.getCategory());
			setDesc(settings.getDescription());
			setQuant(count);
			setColor(settings.getColor());
		}

		public ItemSettings getSettings() {
			return settings;
		}

		public boolean isDisplay() {
			return displayButt.isSelected();
		}

		public void highlight() {
			deselectAllLabels();
			selectedLine = settings;
			itemName.setForeground(Color.blue);
			coverBox.setBorder(BorderFactory.createLineBorder(Color.blue));
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
						requestChange = true;
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
			botLine.add(itemCat);
			botLine.add(itemDesc);

			coverBox.add(topLine);
			coverBox.add(botLine);
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

		// public void setQuant(String value) {
		// itemQuant.setText(value);
		// }

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

}
