package com.plancrawler.elements;

import java.io.Serializable;
import java.util.ArrayList;

import com.plancrawler.utilities.MyPoint;

public class TakeOffManager implements Serializable {

	private static final long serialVersionUID = 1L;
	private ArrayList<Item> items;

	public TakeOffManager() {
		this.items = new ArrayList<Item>();
	}

	public synchronized void wipe() {
		this.items = new ArrayList<Item>();
	}

	public boolean hasItemEntry(String name) {
		boolean answer = false;

		for (Item i : items) {
			if (i.getName().equals(name))
				answer = true;
		}

		return answer;
	}

	public synchronized void addNewItem(Item theItem) {
		items.add(theItem);
	}

	public synchronized void addToItemCount(String name, MyPoint location, int pageNum) {
		Item theItem = getItemByName(name);

		if (theItem == null) {
			makeNewItem(name);
		}

		addToItemCount(theItem, location, pageNum);
	}

	public synchronized void addToItemCount(Item item, MyPoint location, int pageNum) {
		if (item != null) {
			if (!items.contains(item))
				addNewItem(item);
			item.addMark(location, pageNum);
		}
	}

	public Item makeNewItem(String name) {
		Item item = new Item(name);
		items.add(item);
		return item;
	}

	public void subtractItemCount(Item item, MyPoint location, int pageNum) {
		if (item != null)
			item.delMarkAt(location, pageNum);
	}

	public void subtractItemCount(String name, MyPoint location, int pageNum) {
		Item theItem = getItemByName(name);
		subtractItemCount(theItem, location, pageNum);
	}

	public void delItem(Item item) {
		if (item != null)
			items.remove(item);
	}

	public void delItem(String name) {
		Item item = getItemByName(name);
		delItem(item);
	}

	public Item getItemByName(String name) {
		Item theItem = null;

		for (Item i : items) {
			if (i.getName().equals(name))
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
}
