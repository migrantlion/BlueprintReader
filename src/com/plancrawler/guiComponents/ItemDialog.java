package com.plancrawler.guiComponents;

import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.TimeUnit;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.plancrawler.elements.ColorSettings;
import com.plancrawler.elements.Item;

/* enables the opening of a dialog box to enter in the relevant information
 * for the creation of an Item.  It can then return the created Item back to the caller.
 */

public class ItemDialog implements ActionListener {

	private JFrame dialogBox;

	private JComboBox<String> itemPicker;
	private JComboBox<String> catPicker;

	private String[] itemChoices;
	private String[] catChoices;

	private JLabel itemName, catName;

	private JButton colorButt, okButt, cancelButt;

	private ColorSettings colorSettings;
	private Color lineColor = Color.BLUE;
	private Color fillColor = Color.BLUE;
	private float opacity = 1.0f;

	private String iName, cName;
	private boolean okEntry = false;
	private boolean cancelled = false;

	public ItemDialog(String[] iChoices, String[] cChoices) {
		if (iChoices == null) {
			itemChoices = new String[2];
			itemChoices[0] = "-none-";
		} else {
			this.itemChoices = new String[iChoices.length + 1];
			this.itemChoices[0] = "-none-";
			for (int i = 1; i < iChoices.length + 1; i++)
				this.itemChoices[i] = iChoices[i - 1];
		}
		if (cChoices == null) {
			catChoices = new String[2];
			catChoices[0] = "-none-";
		} else {
			this.catChoices = new String[cChoices.length + 1];
			this.catChoices[0] = "-none-";
			for (int i = 1; i < cChoices.length + 1; i++)
				this.catChoices[i] = cChoices[i - 1];
		}
		this.colorSettings = new ColorSettings(lineColor, fillColor, opacity);
		this.dialogBox = new JFrame("Item Selector");
		assemble();
	}

	public Item getItem() {
		Item theItem = null;
		dialogBox.setVisible(true);

		while (!okEntry && !cancelled) {
			try {
				TimeUnit.MICROSECONDS.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (okEntry) {
			theItem = new Item(iName);
			theItem.setCategory(cName);
			theItem.setColorSettings(colorSettings);
		}

		dialogBox.dispose();
		return theItem;
	}

	private void assemble() {
		dialogBox.setSize(350, 300);
		dialogBox.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		Container pane = dialogBox.getContentPane();
		pane.setLayout(new FlowLayout());

		Box itemBox = Box.createHorizontalBox();
		Box catBox = Box.createHorizontalBox();
		Box okBox = Box.createHorizontalBox();

		JLabel ilabel = new JLabel("Choose Item:");
		JLabel clabel = new JLabel("Choose Category:");

		itemPicker = new JComboBox<String>(itemChoices);
		itemPicker.setEditable(true);
		itemPicker.addActionListener(this);

		catPicker = new JComboBox<String>(catChoices);
		catPicker.setEditable(true);
		catPicker.addActionListener(this);

		itemName = new JLabel();
		catName = new JLabel();

		itemBox.add(ilabel);
		itemBox.add(itemPicker);
		itemBox.add(itemName);

		catBox.add(clabel);
		catBox.add(catPicker);
		catBox.add(catName);

		colorButt = new JButton();
		colorButt.setText("Change Color");
		colorButt.setBackground(colorSettings.getFillColor());
		colorButt.setForeground(colorSettings.getInvFillColor());
		colorButt.addActionListener(this);

		okButt = new JButton("OK");
		okButt.addActionListener(this);
		cancelButt = new JButton("CANCEL");
		cancelButt.addActionListener(this);
		okBox.add(okButt);
		okBox.add(cancelButt);

		pane.add(itemBox);
		pane.add(catBox);
		pane.add(colorButt);
		pane.add(okBox);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(colorButt)) {
			colorSettings = ColorPropertyDialog.pickNewColor(colorSettings);
			colorButt.setBackground(colorSettings.getFillColor());
			colorButt.setForeground(colorSettings.getInvFillColor());
		} else if (e.getSource().equals(itemPicker)) {
			iName = (String) itemPicker.getSelectedItem();
			itemName.setText(iName);
		} else if (e.getSource().equals(catPicker)) {
			cName = (String) catPicker.getSelectedItem();
			catName.setText(cName);
		} else if (e.getSource().equals(okButt)) {
			okEntry = true;
		} else if (e.getSource().equals(cancelButt)) {
			cancelled = true;
		}
	}

}
