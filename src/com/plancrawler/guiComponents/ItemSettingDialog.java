package com.plancrawler.guiComponents;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.plancrawler.elements.ItemSettings;

public class ItemSettingDialog{

	public static ItemSettings pickNewSettings(JComponent component, ItemSettings itemSetting) {
		JTextField catField = new JTextField();
		JTextField descField = new JTextField();
		JLabel nameLabel = new JLabel("Information for:    "+itemSetting.getName());
		JLabel catLabel = new JLabel("Item Category :  ");
		JLabel descLabel = new JLabel("Item Description :  ");
		
		JLabel colorLabel = new JLabel("Mark color: ");
		JButton colorButt = new JButton("choose new color");
		colorButt.setBackground(itemSetting.getColor());
		colorButt.setForeground(itemSetting.getInvColor());
		colorButt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource().equals(colorButt)) {
					itemSetting.setColor(
							ColorPropertyDialog.pickNewColor(component, itemSetting.getColor()));
					colorButt.setBackground(itemSetting.getColor());
					colorButt.setForeground(itemSetting.getInvColor());
				}
			}
		}); // end colorButt action listener
		
		catField.setText(itemSetting.getCategory());
		descField.setText(itemSetting.getDescription());
		
		final JComponent[] inputs = new JComponent[] {
				nameLabel,
				new JLabel(" "),
				catLabel,
				catField,
				new JLabel(" "),
				descLabel,
				descField,
				new JLabel(" "),
				colorLabel,
				colorButt,
				new JLabel(" ")
		};
		JOptionPane.showMessageDialog(component, 
				inputs, "Enter "+itemSetting.getName()+" Properties", JOptionPane.QUESTION_MESSAGE);

		itemSetting.setCategory(catField.getText());
		itemSetting.setDescription(descField.getText());
		
		return itemSetting;
	}

}
