package com.plancrawler.guiComponents;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class NavPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private Box buttonBox;
//	private JSlider pageSlider;
	private JLabel pageLabel;
	private JTextField jumpField;
	private int numPages, currentPage, requestedPage;

	public NavPanel() {
		this.numPages = 0;
		this.currentPage = 0;
		this.requestedPage = 0;

		buttonBox = Box.createHorizontalBox();
		addComponents();
		this.add(buttonBox, new FlowLayout());
	}

	private void addComponents() {
		JButton prevButt, nextButt;
		JLabel jumpLabel = new JLabel("   jump to:");

//		pageSlider = new JSlider();
//		pageSlider.setMaximum(numPages);
//		pageSlider.setMinimum(1);
//		pageSlider.setValue(currentPage);
//		pageSlider.addChangeListener(new ChangeListener() {
//			@Override
//			public void stateChanged(ChangeEvent e) {
//				if (e.getSource() == pageSlider) {
//					int newpage = pageSlider.getValue() - 1;
//					changePage(newpage - currentPage);
//					update();
//				}
//			}
//		});

		pageLabel = new JLabel("   Page: " + currentPage + " of " + numPages);

		prevButt = makeNavButton("<|", "PREV_IMAGE");
		nextButt = makeNavButton("|>", "NEXT_IMAGE");

		jumpField = new JTextField(5);
		jumpField.setText(Integer.toString(currentPage +1));
		jumpField.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource().equals(jumpField)) {
					try {
						int newpage = Integer.parseInt(jumpField.getText()) - 1;
						changePage(newpage - currentPage);
					} catch (NumberFormatException ne) {
						jumpField.setText(Integer.toString(currentPage +1));
					}

				}

			}
		});

		
		buttonBox.add(pageLabel);
		buttonBox.add(prevButt);
		buttonBox.add(jumpLabel);
		buttonBox.add(jumpField);
//		buttonBox.add(pageSlider);
		buttonBox.add(nextButt);
	}

//	private void resetSlider() {
//		int value = Math.min(currentPage + 1, numPages);
//		pageSlider.setMaximum(numPages);
//		pageSlider.setValue(value);
//		pageLabel.setText("   Page: " + Integer.toString(value) + " of " + numPages);
//		jumpField.setText(Integer.toString(currentPage+1));
//	}

	public void update() {
		int value = currentPage + 1;
		pageLabel.setText("   Page: " + Integer.toString(value) + " of " + numPages);
	}

	private void changePage(int delta) {
		requestedPage = currentPage + delta;
	}

	public int getRequestedPage() {
		return requestedPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
		this.requestedPage = currentPage;
		jumpField.setText(Integer.toString(currentPage+1));
//		resetSlider();
	}

	public void setNumPages(int numPages) {
		this.numPages = numPages;
//		resetSlider();
	}

	private JButton makeNavButton(String iconFile, String actionName) {
		JButton theButton = new JButton();
		theButton.setText(iconFile);
		// TODO: change this from text to getting an icon
		// Icon buttIcon = new ImageIcon(iconFile);
		// theButton.setIcon(buttIcon);

		theButton.setActionCommand(actionName);
		theButton.addActionListener(new NavButtonListener());
		return theButton;
	}

	private class NavButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			switch (e.getActionCommand()) {
			case "PREV_IMAGE":
				changePage(-1);
				break;
			case "NEXT_IMAGE":
				changePage(+1);
				break;
			}
		}
	}
}
