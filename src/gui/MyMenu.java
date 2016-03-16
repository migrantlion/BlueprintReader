package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.accessibility.Accessible;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.MenuElement;

public class MyMenu extends JMenuItem implements Accessible, MenuElement {

	private static final long serialVersionUID = 1L;
	JMenuBar menuBar = new JMenuBar();

	public MyMenu() {
		// create menus
		JMenu fileMenu = new JMenu("File");

		// create menu items
		JMenuItem pdfMenuItem = new JMenuItem("Open PDF");
		pdfMenuItem.setMnemonic(KeyEvent.VK_P);
		pdfMenuItem.setActionCommand("OpenPDF");

		JMenuItem picsMenuItem = new JMenuItem("Open Images");
		picsMenuItem.setActionCommand("OpenImage");

		JMenuItem saveMenuItem = new JMenuItem("Save");
		saveMenuItem.setActionCommand("Save");

		JMenuItem exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.setActionCommand("Exit");

		MenuItemListener menuItemListener = new MenuItemListener();
		pdfMenuItem.addActionListener(menuItemListener);
		picsMenuItem.addActionListener(menuItemListener);
		saveMenuItem.addActionListener(menuItemListener);
		exitMenuItem.addActionListener(menuItemListener);
		
		// add menu items to menus
		fileMenu.add(pdfMenuItem);
		fileMenu.add(picsMenuItem);
		fileMenu.add(saveMenuItem);
		fileMenu.add(exitMenuItem);
		
		// add menu to the bar
		menuBar.add(fileMenu);
	}

	private class MenuItemListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println(e.getActionCommand() + " menu item clicked!");
		}

	}
}
