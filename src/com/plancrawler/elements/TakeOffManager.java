package com.plancrawler.elements;

import java.io.Serializable;
import java.util.ArrayList;

import com.plancrawler.utilities.MyPoint;

public class TakeOffManager implements Serializable {

	private static final long serialVersionUID = 1L;
	private ArrayList<Item> items;
	private boolean hasChanged = false;

	public TakeOffManager() {
		this.items = new ArrayList<Item>();
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
}
