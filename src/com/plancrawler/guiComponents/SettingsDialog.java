package com.plancrawler.guiComponents;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.plancrawler.elements.Settings;

public class SettingsDialog{

	public static Settings pickNewSettings(JComponent component, Settings itemSetting) {
		if (itemSetting == null)
			itemSetting = new Settings("name");
		final Settings setting = itemSetting;
		
		JTextField nameField = new JTextField(setting.getName());
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
					setting.setColor(
							ColorPropertyDialog.pickNewColor(component, setting.getColor()));
					colorButt.setBackground(setting.getColor());
					colorButt.setForeground(setting.getInvColor());
				}
			}
		}); // end colorButt action listener
		
		catField.setText(setting.getCategory());
		descField.setText(setting.getDescription());
		
		final JComponent[] inputs = new JComponent[] {
				nameLabel,
				nameField,
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
				inputs, "Enter "+setting.getName()+" Properties", JOptionPane.QUESTION_MESSAGE);

		setting.setName(nameField.getText());
		setting.setCategory(catField.getText());
		setting.setDescription(descField.getText());
		
		return setting;
	}

}
