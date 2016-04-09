package com.plancrawler.elements;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import com.plancrawler.guiComponents.ChalkBoardMessage;
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
				createNewItem(s);
			}
		}
	}
	
	public void wipe() {
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
	
	public void addNewItem(Item theItem) {
		items.add(theItem);
	}

	public void addToItemCount(String name, MyPoint location, int pageNum) {
		Item theItem = getItemByName(name);
		
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
	
	public void readMessage(ArrayList<ChalkBoardMessage> message) {
		for (ChalkBoardMessage m : message) {
			if (!hasItemEntry(m.getName())) {
				Item i = this.createNewItem(m.getName());
				i.setSettings(new ItemSettings(m.getName(),m.getComments(),m.getColor()));
				this.addNewItem(i);
			} else {
				Item i = getItemByName(m.getName());
				i.setSettings(new ItemSettings(i.getName(),m.getComments(),m.getColor()));
			}
		}
	}
	
	public ArrayList<ChalkBoardMessage> sendMessage(){
		ArrayList<ChalkBoardMessage> message = new ArrayList<ChalkBoardMessage>();
		
		for (Item i : items) {
			ChalkBoardMessage m = new ChalkBoardMessage();
			m.setName(i.getName());
			m.setQuantity(i.count());
			m.setComments(i.getDescription());
			m.setColor(i.getColorSettings().getFillColor());
			message.add(m);
		}
		return message;
	}
}
