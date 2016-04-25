package com.plancrawler.elements;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import com.plancrawler.gui.Paintable;
import com.plancrawler.utilities.MyPoint;
import com.plancrawler.warehouse.ShowRoom;
import com.plancrawler.warehouse.Warehouse;

public class TakeOffManager implements Serializable {

	private static final long serialVersionUID = 1L;
	private ArrayList<Item> items;
	private boolean hasChanged = false;
	private static TakeOffManager uniqueInstance = new TakeOffManager();
	private ShowRoom showroom = new ShowRoom();
	private Warehouse warehouse = Warehouse.getInstance();
	private String pdfName;

	private TakeOffManager() {
		this.items = new ArrayList<Item>();
	}
	
	public static TakeOffManager getInstance() {
		return uniqueInstance;
	}
	
	public void update() {
		showroom.update();
	}

	public synchronized void wipe() {
		this.items = new ArrayList<Item>();
	}

	public boolean hasItemEntry(Settings activeItemName) {
		boolean answer = false;

		for (Item i : items) {
			if (i.getSettings().equals(activeItemName))
				answer = true;
		}

		return answer;
	}

	public void addCrateToTakeOff(Settings crateInfo, MyPoint loc, int page) {
		showroom.addToShowRoom(crateInfo, loc, page);
		hasChanged = true;
	}
	
	public void removeCrateFromTakeOff(Settings crateInfo, MyPoint loc, int page) {
		showroom.removeFromShowRoom(crateInfo, loc, page);
		hasChanged = true;
	}
	
	public synchronized void addNewItem(Item theItem) {
		items.add(theItem);
		hasChanged = true;
	}

	public synchronized void addToItemCount(Settings activeItemName, MyPoint location, int pageNum) {
		Item item = getItemBySetting(activeItemName);

		if (item == null) {
			item = makeNewItem(activeItemName);
		}
		item.addMark(location, pageNum);
		hasChanged = true;
	}

	public synchronized Item makeNewItem(Settings settings) {
		Item item = new Item(settings);
		items.add(item);
		hasChanged = true;
		return item;
	}

	public synchronized void subtractItemCount(Settings setting, MyPoint location, int pageNum) {
		Item item = getItemBySetting(setting);
		if (item != null) {
			item.delMarkAt(location, pageNum);
			hasChanged = true;
		}
	}

	public void delItem(Item item) {
		if (item != null)
			items.remove(item);
		hasChanged = true;
	}

	public Item getItemBySetting(Settings setting) {
		Item theItem = null;

		for (Item i : items) {
			if (i.getSettings().equals(setting))
				theItem = i;
		}

		return theItem;
	}

	public String[] getItemNames() {
		if (items.size() == 0)
			return null;

		String[] names = new String[items.size()];
		int i = 0;
		for (Item item : items) {
			names[i] = item.getName();
			i++;
		}
		return names;
	}

	public synchronized ArrayList<Item> getItems() {
		return items;
	}

	public void displayItems() {
		for (Item i : items) {
			System.out.println(i.getName() + " / " + i.getCategory() + " : " + i.count());
		}
	}
	
	public HashMap<Settings, Integer> getShowroomItems(){
		showroom.update();
		return showroom.getItems();
	}

	public boolean hasItemName(String name) {
		boolean answer = false;
		for (Item item : items)
			if (item.getName().equals(name))
				answer = true;
		return answer;
	}

	public synchronized void delItemBySetting(Settings setting) {
		Item item = getItemBySetting(setting);
		if (item == null)
			return;
		else
			delItem(item);
		hasChanged = true;
	}

	public boolean hasChanged() {
		boolean changed = hasChanged;
		hasChanged = false;
		return changed;
	}

	public void setChanged(boolean state) {
		hasChanged = state;
	}

	public ArrayList<Paintable> getShowroomMarks(int page) {
		ArrayList<Paintable> displayCrates = new ArrayList<Paintable>();
		displayCrates.addAll(showroom.getCrates(page));
		return displayCrates;
	}
	
	public ArrayList<Paintable> getWarehouseMarks(int page) {
		ArrayList<Paintable> displayCrates = new ArrayList<Paintable>();
		displayCrates.addAll(warehouse.getCrateItems(page));
		return displayCrates;
	}

	public void setPDFName(String pdfName) {
		this.pdfName = pdfName;
	}
	
	public String getPDFName() {
		return pdfName;
	}

	public Warehouse getWarehouse() {
		return warehouse;
	}
}
