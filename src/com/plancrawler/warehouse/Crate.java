package com.plancrawler.warehouse;

import java.awt.Color;
import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.plancrawler.elements.Item;
import com.plancrawler.elements.Settings;
import com.plancrawler.elements.StorageItem;
import com.plancrawler.utilities.MyPoint;

public class Crate implements Serializable {

	private static final long serialVersionUID = 1L;

	// private final String imagePath = "res/wooden-crate.png";

	private CopyOnWriteArrayList<StorageItem> looseItems;
	private CopyOnWriteArrayList<Crate> otherCrates;
	private Settings settings;

	public Crate(Settings settings) {
		this.settings = settings;
		looseItems = new CopyOnWriteArrayList<StorageItem>();
		otherCrates = new CopyOnWriteArrayList<Crate>();
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

	public void addNewItemToCrate(StorageItem item) {
		if (item != null && !looseItems.contains(item))
			looseItems.add(item);
	}

	public void addToCrate(Settings itemInfo, MyPoint loc, int pageNum) {
		if (!crateHasItem(itemInfo))
			addNewItemToCrate(itemInfo);
		StorageItem item = null;
		for (Item i : looseItems)
			if (i.getSettings().equals(itemInfo))
				item = (StorageItem) i;

		if (item != null)
			item.addMark(loc, pageNum);
	}

	public void addCrateToCrate(Crate crate) {
		otherCrates.add(crate);
	}

	public boolean crateHasItem(Settings itemInfo) {
		boolean answer = false;
		for (Item i : looseItems)
			if (i.getSettings().equals(itemInfo))
				answer = true;
		return answer;
	}

	public boolean crateHasCrate(Settings crateInfo) {
		boolean answer = false;
		for (Crate c : otherCrates)
			if (c.getSettings().equals(crateInfo))
				answer = true;
		return answer;
	}

	public void addNewItemToCrate(Settings setting) {
		StorageItem item = makeNewCrateItem(setting);
		addNewItemToCrate(item);
	}

	private StorageItem makeNewCrateItem(Settings setting) {
		StorageItem item = new StorageItem(setting, this.settings.getColor());
		return item;
	}

	public HashMap<Settings, Integer> unwrap() {
		HashMap<Settings, Integer> summary = new HashMap<Settings, Integer>();
		summary = unwrap(null);
		return summary;
	}

	private HashMap<Settings, Integer> unwrap(HashMap<Settings, Integer> summary) {
		if (summary == null)
			summary = new HashMap<Settings, Integer>();

		for (Item item : looseItems) {
			Settings setting = item.getSettings();
			if (summary.containsKey(setting))
				summary.put(setting, item.count() + summary.get(setting));
			else
				summary.put(setting, item.count());
		}

		for (Crate c : otherCrates) {
			summary = c.unwrap(summary);
		}

		return summary;
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
			sItem.setMarkQuantity(1);
			sItem.setMarkDisplay(false);
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

	public void putSettings(Settings settings2) {
		this.settings = settings2;
	}

	public int rawCount() {
		int total = 0;

		for (Item i : looseItems)
			total += i.getMarks().size();

		total += otherCrates.size();

		return total;
	}

	public CopyOnWriteArrayList<StorageItem> getLooseItems() {
		return looseItems;
	}

	public CopyOnWriteArrayList<Crate> getOtherCrates() {
		return otherCrates;
	}

	private Item getItem(Settings itemInfo) {
		Item item = null;
		for (Item i : looseItems)
			if (i.getSettings().equals(itemInfo))
				item = i;
		return item;
	}

	public void removeItem(Settings setting, MyPoint point, int page) {
		if (crateHasItem(setting)) {
			getItem(setting).delMarkAt(point, page);
		} 
	}

}
