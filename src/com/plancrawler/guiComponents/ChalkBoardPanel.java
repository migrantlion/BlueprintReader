package com.plancrawler.guiComponents;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ChalkBoardPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	private ArrayList<Box> boxes;
	private JLabel entryLabel;
	private JTextField itemEntry;
	private JPanel board;
	private String selectedLine;
	JButton delButt; 
	private HashMap<String,Integer> nameToCountMap;

	public ChalkBoardPanel(int width, int height) {
		this.setSize(width, height);
		this.setLayout(new FlowLayout());
		boxes = new ArrayList<Box>();
		prepBoard();
		this.add(board);
		
		this.nameToCountMap = new HashMap<String,Integer>();
	}

	public void prepBoard() {
		board = new JPanel();
		board.setSize(this.getSize());
		board.setLayout(new BoxLayout(board, BoxLayout.Y_AXIS));

		Box box = Box.createHorizontalBox();

		entryLabel = new JLabel("new item: ");
		box.add(entryLabel);
		itemEntry = new JTextField(15);
		itemEntry.addActionListener(this);
		box.add(itemEntry);
		
		delButt = new JButton("[-]");
		delButt.addActionListener(this);
		box.add(delButt);

		board.add(box);

		Box box2 = Box.createHorizontalBox();
		JLabel separator = new JLabel("----items---");
		box2.add(separator);

		board.add(box2);
	}
	
	public void setNameToCountMap(HashMap<String,Integer> map) {
		this.nameToCountMap = map;
		updateLabels();
	}
	
	public String[] getAllItemLines() {
		String[] itemNames = new String[boxes.size()];
		int i = 0;
		for (Box b : boxes) {
			itemNames[i] = b.getName();
			i++;
		}
		
		return itemNames;
	}
	
	public String getSelectedLine() {
		return selectedLine;
	}

	private boolean isOnBoard(String name) {
		boolean answer = false;
		for (Box b : boxes) {
			if (b.getName().equals(name))
				answer = true;
		}

		return answer;
	}

	// this method to be overwritten if need special text on board
	private String setLabelText(String name) {
		String label;
		
		if (nameToCountMap.containsKey(name)) {
			label = name + " : (" + nameToCountMap.get(name) + ") ";
		} else
			label = name;
		
		return label;
	}
	
	private void updateLabels() {
		for (Box b : boxes) {
			JLabel label = (JLabel) b.getComponent(0);
			label.setText(setLabelText(b.getName()));		
		}
	}
	
	private void addLabel(final String text) {
		if (!isOnBoard(text)) {
			final Box box = Box.createHorizontalBox();
			box.setName(text);
			box.addMouseListener(new CBMouseListener());
			
			final JLabel label = new JLabel(setLabelText(text));
			
			box.add(label);
			board.add(box);

			boxes.add(box);
			deselectAllLabels();

			validate();
			repaint();
		}
	}

	private void selectLabel(Box box) {
		deselectAllLabels();
		selectedLine = box.getName();
		
		JLabel label = (JLabel) box.getComponent(0);
		
		label.setForeground(Color.red);

		validate();
		repaint();
	}

	private void deselectAllLabels() {
		for (Box b : boxes) {
			JLabel label = (JLabel) b.getComponent(0);
			label.setForeground(Color.black);
		}
		selectedLine = null;

		validate();
		repaint();
	}
	
	private void removeSelectedLine() {
		if (selectedLine == null)
			return;
		else {
			Box rmbox = null;
			for (Box b : boxes) {
				if (b.getName().equals(selectedLine))
					rmbox = b;
			}
			if (rmbox != null) {
				boxes.remove(rmbox);
				board.remove(rmbox);
				validate();
				repaint();
			}
			deselectAllLabels();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(itemEntry)) {
			String iName = itemEntry.getText();
			itemEntry.setText(null);
			addLabel(iName);
		} else if (e.getSource().equals(delButt)) {
			removeSelectedLine();
		}
	}

	private class CBMouseListener extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			Box box = (Box) e.getSource();
			String text = box.getName();

			if (text.equals(selectedLine)) {
				deselectAllLabels();
			} else {
				selectLabel(box);
			}
		}
	}
}
