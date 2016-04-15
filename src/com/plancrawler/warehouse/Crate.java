package com.plancrawler.warehouse;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;

import com.plancrawler.elements.Item;
import com.plancrawler.elements.Settings;
import com.plancrawler.elements.StorageItem;
import com.plancrawler.utilities.Helpers;

public class Crate implements Serializable {

	private static final long serialVersionUID = 1L;

	// private final String imagePath = "res/wooden-crate.png";

	private ArrayList<Item> looseItems;
	private ArrayList<Crate> otherCrates;
	private Settings settings;

	public Crate(Settings settings) {
		this.settings = settings;
		looseItems = new ArrayList<Item>();
		otherCrates = new ArrayList<Crate>();
	}

	public Crate(String name) {
		this(new Settings(name));
	}

	public Crate(Crate copy) {
		// make a deep copy of the crate with all item quantities turned on
		this(copy.settings);
		looseItems.addAll(copy.looseItems);	
		for (Crate c : copy.otherCrates)
			otherCrates.add(new Crate(c));
	}
	
	public void addNewItemToCrate(Item item) {
		if (item != null && !looseItems.contains(item))
			looseItems.add(item);
	}

	public void addNewItemToCrate(Settings setting) {
		Item item = makeNewCrateItem(setting);
		addNewItemToCrate(item);
	}

	private Item makeNewCrateItem(Settings setting) {
		Item item = new StorageItem(setting, this.settings.getColor());
		return item;
	}

	public ArrayList<Item> unwrap() {
		ArrayList<Item> contents = new ArrayList<Item>();
		contents.addAll(looseItems);

		for (Crate c : otherCrates)
			contents = Helpers.combineItemArrays(contents, c.unwrap());

		return contents;
	}
	
	public void putItemsInStorage() {
		for (Item item : looseItems) {
			StorageItem sItem = (StorageItem) item;
			sItem.setMarkQuantity(0);
			sItem.setMarkDisplay(true);
		}
	}
	
	public Crate pullCopyFromStorage() {
		// make a deep copy of the crate with all item quantities turned on
		Crate newCrate = new Crate(settings);
		
		newCrate.looseItems.addAll(this.looseItems);
		for (Item item : newCrate.looseItems) {
			StorageItem sItem = (StorageItem) item;
			sItem.setMarkQuantity(0);
			sItem.setMarkDisplay(true);
		}
		
		for (Crate c : this.otherCrates)
			newCrate.otherCrates.add(c.pullCopyFromStorage());
		
		return newCrate;
	}

	public String getName() {
		return settings.getName();
	}
	
	public Settings getSettings() {
		return settings;
	}
	
	public Color getColor() {
		return settings.getColor();
	}
}
