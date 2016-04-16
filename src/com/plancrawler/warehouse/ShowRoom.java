package com.plancrawler.warehouse;

import java.util.ArrayList;
import java.util.HashMap;

import com.plancrawler.elements.Settings;
import com.plancrawler.utilities.MyPoint;

public class ShowRoom {

	private Warehouse wareHouse;
	private ArrayList<DisplayCrate> showRoom;

	public ShowRoom() {
		this.wareHouse = Warehouse.getInstance();
		this.showRoom = new ArrayList<DisplayCrate>();
	}

	public void update() {
		if (wareHouse.hasChanged())
			updateShowRoom();
	}
	
	public void addToShowRoom(Settings crateInfo, MyPoint loc, int pageNum) {
		Crate crate = wareHouse.pullCrateFromWareHouse(crateInfo);
		if (crate != null) {
			DisplayCrate dc = new DisplayCrate(crate, loc, pageNum);
			showRoom.add(dc);
		}
	}
	
	public void removeFromShowRoom(Settings crateInfo, MyPoint loc, int pageNum) {
		DisplayCrate dc = getDisplayCrate(crateInfo, loc, pageNum);
		if (dc != null)
			showRoom.remove(dc);
	}
	
	public HashMap<Settings,Integer> getItems(){
		HashMap<Settings,Integer> summary = new HashMap<Settings,Integer>();
		
		for (DisplayCrate dc : showRoom) {
			HashMap<Settings,Integer> dcsum = new HashMap<Settings,Integer>();
			dcsum = dc.unwrap();
			for (Settings info : dcsum.keySet()) {
				if (summary.containsKey(info))
					summary.put(info, summary.get(info) + dcsum.get(info));
				else
					summary.put(info, dcsum.get(info));
			}
		}
		
		return summary;
	}
	
	private DisplayCrate getDisplayCrate(Settings crateInfo, MyPoint loc, int pageNum) {
		DisplayCrate crate = null;
		for (DisplayCrate dc : showRoom)
			if (dc.getSettings().equals(crateInfo) && dc.atLocation(loc,pageNum) )
				crate = dc;
		return crate;
	}

	private void updateShowRoom() {
		// will be modifying showRoom, so need a copy to iterate over
		ArrayList<DisplayCrate> iterator = new ArrayList<DisplayCrate>();
		iterator.addAll(showRoom);

		for (DisplayCrate dc : iterator) {
			// pull a copy of this crate from the warehouse
			Crate whCopy = wareHouse.pullCrateFromWareHouse(dc.getSettings());
			if (whCopy == null)
				System.out.println("Error in update ShowRoom getting " + dc.getName());
			else {
				// remove the old crate
				showRoom.remove(dc);
				// replace with the updated copy in same location as old one
				showRoom.add(new DisplayCrate(whCopy, dc.getLocation(), dc.getPageNum()));
			}
		}
	}

}
