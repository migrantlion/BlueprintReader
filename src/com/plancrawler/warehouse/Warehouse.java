package com.plancrawler.warehouse;

import java.io.Serializable;
import java.util.ArrayList;

import com.plancrawler.elements.Settings;

public class Warehouse implements Serializable {
	// singleton class with eager instantiation
	private static Warehouse uniqueInstance = new Warehouse();
	
	private static final long serialVersionUID = 1L;
	private ArrayList<Crate> crates;
	private boolean hasChanged = false;
	
	private Warehouse() {
		this.crates = new ArrayList<Crate>();
	}
	
	public static Warehouse getInstance() {
		return uniqueInstance;
	}
	
	public synchronized void clear() {
		this.crates = new ArrayList<Crate>();
	}

	public void addItemToCrate(Settings crateInfo, Settings itemInfo) {
		if (crateInWareHouse(crateInfo)) 
			getCrate(crateInfo).addNewItemToCrate(itemInfo);
		else {
			Crate crate = new Crate(crateInfo);
			crate.addNewItemToCrate(itemInfo);
			registerNewCrate(crate);
		}
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

	private boolean crateInWareHouse(String name) {
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
	
	public Crate pullCrateFromWareHouse(Settings crateInfo) {
		Crate crate = null;
		if (crateInWareHouse(crateInfo))
			crate = getCrate(crateInfo).pullCopyFromStorage();
		return crate;
	}
	
	public ArrayList<Crate> getInventory(){
		ArrayList<Crate> inventory = new ArrayList<Crate>();
		inventory.addAll(crates);
		return inventory;
	}

}
