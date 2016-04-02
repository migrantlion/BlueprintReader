package com.plancrawler.elements;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import com.plancrawler.utilities.MyPoint;

public class TakeOffManager implements Serializable {

	private static final long serialVersionUID = 1L;
	private ArrayList<Item> items;

	public TakeOffManager() {
		this.items = new ArrayList<Item>();
	}

	public void batchAddItems(String[] names) {
		for (String s : names) {
			if (!hasItemEntry(s)) {
				addNewItem(createNewItem(s));
			}
		}
	}
	
	public boolean hasItemEntry(String name) {
		boolean answer = false;
		
		for (Item i : items) {
			if (i.getName().equals(name))
				answer = true;
		}
		
		return answer;
	}
	
	public void addNewItem(Item theItem) {
		items.add(theItem);
	}

	public void addToItemCount(String name, MyPoint location, int pageNum) {
		Item theItem = getItem(name);
		
		if (theItem == null) {
			theItem = createNewItem(name);
		}

		addToItemCount(theItem, location, pageNum);
	}

	public void addToItemCount(Item item, MyPoint location, int pageNum) {
		if (item != null) {
			if (!items.contains(item))
				addNewItem(item);
			item.addMark(location, pageNum);
		}
	}
	
	public Item createNewItem(String name) {
		return new Item(name);
	}

	public void subtractItemCount(Item item, MyPoint location, int pageNum) {
		if (item != null)
			item.delMarkAt(location, pageNum);
	}

	public void subtractItemCount(String name, MyPoint location, int pageNum) {
		Item theItem = getItem(name);
		subtractItemCount(theItem, location, pageNum);
	}

	public void delItem(Item item) {
		if (item != null)
			items.remove(item);
	}

	public void delItem(String name) {
		Item item = getItem(name);
		delItem(item);
	}

	public Item getItem(String name) {
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

	public String[] getCategoryNames() {
		if (items.size() == 0)
			return null;
		
		String[] names = new String[items.size()];
		int i = 0;
		for (Item item : items) {
			names[i] = item.getCategory();
			i++;
		}
		
		return names;
	}

	public ArrayList<Item> getItems() {
		return items;
	}

	public void displayItems() {
		for (Item i : items) {
			System.out.println(i.getName() + " / " + i.getCategory() + " : " + i.count());
		}
	}
	
	public HashMap<String,Integer> summaryCount(){
		HashMap<String,Integer> summary = new HashMap<String,Integer>();
		
		for (Item i : items) {
			summary.put(i.getName(), i.count());
		}
		
		return summary;
	}
}
