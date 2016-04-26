package com.plancrawler.warehouse;

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;

import com.plancrawler.elements.Item;
import com.plancrawler.elements.Settings;
import com.plancrawler.gui.Paintable;
import com.plancrawler.utilities.MyPoint;

public class Warehouse implements Serializable {
	// singleton class with eager instantiation
	private static Warehouse uniqueInstance = new Warehouse();

	private static final long serialVersionUID = 1L;
	private CopyOnWriteArrayList<Crate> crates;
	private boolean hasChanged = false;

	private Warehouse() {
		this.crates = new CopyOnWriteArrayList<Crate>();
	}

	public static Warehouse getInstance() {
		return uniqueInstance;
	}

	public synchronized void clear() {
		crates.clear();
		hasChanged = true;
	}

	public void addItemToCrate(Settings crateInfo, Settings itemInfo, MyPoint point, int page) {
		if (crateInWareHouse(crateInfo))
			getCrate(crateInfo).addToCrate(itemInfo, point, page);
		else {
			Crate crate = new Crate(crateInfo);
			crate.addToCrate(itemInfo, point, page);
			registerNewCrate(crate);
		}
		hasChanged = true;
	}

	public Crate getCrate(Settings crateInfo) {
		Crate crate = null;
		for (Crate c : crates)
			if (c.getSettings().equals(crateInfo))
				crate = c;
		return crate;
	}

	public void adddNewCrate(Settings setting) {
		if (!crateInWareHouse(setting)) {
			Crate crate = new Crate(setting);
			registerNewCrate(crate);
		}

	}

	public void adddNewCrate(String name) {
		if (!crateInWareHouse(name)) {
			Crate crate = new Crate(name);
			registerNewCrate(crate);
		}
	}

	public void registerNewCrate(Crate crate) {
		crates.add(crate);
		hasChanged = true;
	}

	public boolean crateInWareHouse(String name) {
		boolean answer = false;
		for (Crate c : crates)
			if (c.getName().equals(name))
				answer = true;
		return answer;
	}

	public boolean crateInWareHouse(Settings crateInfo) {
		boolean answer = false;
		for (Crate c : crates)
			if (c.getSettings().equals(crateInfo))
				answer = true;
		return answer;
	}

	public boolean hasChanged() {
		boolean state = hasChanged;
		hasChanged = false;
		return state;
	}

	public void setChanged(boolean state) {
		this.hasChanged = state;
	}

	public Crate pullCrateFromWareHouse(Settings crateInfo) {
		Crate crate = null;
		if (crateInWareHouse(crateInfo))
			crate = getCrate(crateInfo).pullCopyFromStorage();
		return crate;
	}

	public CopyOnWriteArrayList<Crate> getInventory() {
		CopyOnWriteArrayList<Crate> inventory = new CopyOnWriteArrayList<Crate>();
		inventory.addAll(crates);
		return inventory;
	}

	public CopyOnWriteArrayList<Paintable> getCrateItems(int page) {
		CopyOnWriteArrayList<Paintable> paintList = new CopyOnWriteArrayList<Paintable>();
		for (Crate c : crates) {
			for (Item i : c.getLooseItems())
				paintList.addAll(i.getMarks(page));
		}
		return paintList;
	}

	public void delItemFromCrate(Settings activeCrateName, Settings activeItemName, MyPoint point, int currentPage) {
		if (crateInWareHouse(activeCrateName)) {
			Crate crate = getCrate(activeCrateName);
			crate.removeItem(activeItemName, point, currentPage);
			hasChanged = true;
		}
	}

	public void delCrateBySetting(Settings crateInfo) {
		if (crateInWareHouse(crateInfo)) {
			Crate crate = getCrate(crateInfo);
			crates.remove(crate);
			hasChanged = true;
		}
	}

}
