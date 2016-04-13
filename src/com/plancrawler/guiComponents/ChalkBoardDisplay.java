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
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import com.plancrawler.elements.ColorSettings;
import com.plancrawler.elements.Item;

public class ChalkBoardDisplay extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	private String title;
	private ArrayList<CBEntry> entries;

	private JLabel entryLabel;
	private JTextField itemEntry;
	private JPanel board;
	private String selectedLine, removeLine;
	private JButton sortButt;
	private JButton delButt;

	public ChalkBoardDisplay(String title, int width, int height, JButton delButt) {
		this.title = title;
		this.setSize(width, height);
		this.setLayout(new FlowLayout());
		this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		this.delButt = delButt;
		entries = new ArrayList<CBEntry>();
		prepBoard();
		this.add(board);
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

		Box box = Box.createVerticalBox();
		JLabel titleLabel = new JLabel(title);

		Box topBox = Box.createHorizontalBox();
		Box bottBox = Box.createHorizontalBox();

		entryLabel = new JLabel("new item: ");
		topBox.add(entryLabel);
		itemEntry = new JTextField(15);
		itemEntry.addActionListener(this);
		topBox.add(itemEntry);

		delButt.addActionListener(this);
		bottBox.add(delButt);
		sortButt = new JButton("sort");
		sortButt.addActionListener(this);
		bottBox.add(sortButt);

		box.add(titleLabel);
		box.add(new JLabel("  ")); // blank line
		box.add(topBox);
		box.add(bottBox);

		board.add(box);

		Box box2 = Box.createHorizontalBox();
		JLabel separator = new JLabel("----items---");
		box2.add(separator);

		board.add(box2);
	}

	public String getSelectedLine() {
		return selectedLine;
	}

	public void clearCounts() {
		for (CBEntry e : entries)
			e.setQuant(0);
	}

	private boolean isOnBoard(String name) {
		boolean answer = false;
		for (CBEntry e : entries) {
			if (e.getName().equals(name))
				answer = true;
		}
		return (answer && !isToBeRemoved(name));
	}

	private synchronized void eraseBoard() {
		// detach all the entries from the board
		for (CBEntry e : entries)
			board.remove(e.coverBox);
	}

	private void sortEntries() {
		// detach all the JLabel boxes from the board and then re-Attach in alph
		// order
		// by category, then by item name
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
			if (!isOnBoard(i.getName()))
				addEntry(i.getName());
			if (getCBEntry(i.getName()) != null)
				getCBEntry(i.getName()).update(i.getCategory(), i.getDescription(), i.getColor(), i.count());
		}
		validate();
		repaint();
	}

	private synchronized CBEntry getCBEntry(String name) {
		CBEntry entry = null;
		for (CBEntry e : entries)
			if (e.getName().equals(name))
				entry = e;
		return entry;
	}
	
	private boolean isToBeRemoved(String name) {
		return (removeLine != null && removeLine.equals(name));
	}

	private synchronized void addEntry(final String text) {
		if (!isOnBoard(text) && !isToBeRemoved(text)) {
			CBEntry entry = new CBEntry(text, "none");
			entries.add(entry);
			board.add(entry.coverBox);
		}
	}

	public void deselectAllLabels() {
		for (CBEntry e : entries) {
			e.plain();
		}
		selectedLine = null;

		validate();
		repaint();
	}

	public boolean isDisplay(String name) {
		if (isOnBoard(name)) {
			return getCBEntry(name).isDisplay();
		} else {
			System.out.println("No item " + name + " found in ChalkBoard");
			return false;
		}
	}

	private synchronized void removeSelectedLine() {
		if (selectedLine == null)
			return;
		else {
			CBEntry e = getCBEntry(selectedLine);
			removeLine = selectedLine;
			selectedLine = null;
			if (e != null) {
				entries.remove(e);
				board.remove(e.coverBox);

				validate();
				repaint();
			}
		}
	}

	public String getRemoveLine() {
		return removeLine;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(itemEntry)) {
			String iName = itemEntry.getText();
			itemEntry.setText(null);
			addEntry(iName);
		} else if (e.getSource().equals(sortButt)) {
			sortEntries();
		} else if (e.getSource().equals(delButt)) {
			removeSelectedLine();
		}
	}

	private class CBEntry extends MouseAdapter implements Comparable<CBEntry> {
		private String name;
		private String category;
		private JLabel itemName;
		private JLabel itemQuant;
		private JLabel itemDesc;
		private JLabel itemCat;
		private JButton colorButt;
		private JRadioButton displayButt;
		private JPanel coverBox;

		public CBEntry(String name, String category) {
			this.name = name;
			this.category = category;
			setupItems();
			highlight();
		}

		public boolean isDisplay() {
			return displayButt.isSelected();
		}

		public void update(String category, String description, Color color, int count) {
			setCategory(category);
			setDesc(description);
			setQuant(count);
			setColor(color);
		}

		public void highlight() {
			removeLine = null;
			deselectAllLabels();
			selectedLine = getName();
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
			coverBox.setName(name);
			coverBox.addMouseListener(this);

			itemName = new JLabel(name);
			itemName.setName(name + ".name");
			itemName.setHorizontalAlignment(JLabel.CENTER);

			itemQuant = new JLabel("0");
			itemQuant.setName(name + ".quant");
			itemQuant.setHorizontalAlignment(JLabel.CENTER);

			itemDesc = new JLabel("---");
			itemDesc.setName(name + ".desc");
			itemDesc.setHorizontalAlignment(JLabel.CENTER);

			itemCat = new JLabel(category + ": ");
			itemCat.setName(name + ".cat");
			itemCat.setHorizontalAlignment(JLabel.LEFT);

			colorButt = new JButton(".");
			Color color = new Color(255, 255, 255);
			colorButt.setBackground(color);
			colorButt.setName(name + ".colorButt");
			colorButt.setForeground(color);

			displayButt = new JRadioButton("on", true);
			displayButt.setName(name + ".displayButt");
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

		public String getName() {
			return name;
		}

		public String getCategory() {
			return category;
		}

		public void setCategory(String category) {
			this.category = category;
			itemCat.setText(category + ": ");
		}

		public void setDesc(String desc) {
			itemDesc.setText(desc);
		}

		public void setQuant(int count) {
			itemQuant.setText(Integer.toString(count));
		}

//		public void setQuant(String value) {
//			itemQuant.setText(value);
//		}

		public void setColor(Color color) {
			colorButt.setBackground(color);
			colorButt.setForeground(color);
		}

		public void mouseClicked(MouseEvent e) {

			if (getName().equals(selectedLine)) {
				deselectAllLabels();
			} else {
				highlight();
			}
		}

		@Override
		public int compareTo(CBEntry other) {
			if (this.category.compareTo(other.getCategory()) == 0)
				return this.name.compareTo(other.getName());
			else
				return this.category.compareTo(other.getCategory());
		}

	}

}
