package testEngine;

import java.util.HashMap;

import com.plancrawler.elements.Item;
import com.plancrawler.elements.Settings;
import com.plancrawler.utilities.MyPoint;
import com.plancrawler.warehouse.Crate;
import com.plancrawler.warehouse.ShowRoom;
import com.plancrawler.warehouse.Warehouse;

public class TestEngine {

	public static void main(String[] args) {

//		GUI gui = new GUI();
//		gui.init();
//
//		boolean running = true;
//		while (running) {
//			gui.updateComponents();
//		}
		
		Warehouse warehouse = Warehouse.getInstance();
		ShowRoom showroom = new ShowRoom();
		
		Settings pizza = new Settings("pizza");
		Settings steak = new Settings("steak");
		Settings macaroni = new Settings("macaroni");
		
		Crate testCrate = new Crate("testCrate");
		testCrate.addNewItemToCrate(pizza);
		testCrate.addToCrate(pizza, new MyPoint(0,0), 0);
		testCrate.addToCrate(pizza, new MyPoint(10,10), 0);
		warehouse.registerNewCrate(testCrate);
		
		Crate testCrate2 = new Crate("testCrate");
		testCrate2.addToCrate(steak, new MyPoint(0,0), 0);
		testCrate2.addToCrate(macaroni, new MyPoint(10,10), 0);
		warehouse.registerNewCrate(testCrate2);
		
		HashMap<Settings, Integer> items = showroom.getItems();
		System.out.println("Showroom items:");
		for (Settings i : items.keySet()) {
			System.out.println("Item "+i.getName()+" :  "+items.get(i));
		}
		
		showroom.addToShowRoom(testCrate.getSettings(), new MyPoint(5,0), 3);
		items = showroom.getItems();
		System.out.println("Showroom items:");
		for (Settings i : items.keySet()) {
			System.out.println("Item "+i.getName()+" :  "+items.get(i));
		}
		
		showroom.addToShowRoom(testCrate2.getSettings(), new MyPoint(5,5), 3);
		showroom.addToShowRoom(testCrate2.getSettings(), new MyPoint(5,0), 3);
		items = showroom.getItems();
		System.out.println("Showroom items:");
		for (Settings i : items.keySet()) {
			System.out.println("Item "+i.getName()+" :  "+items.get(i));
		}
		
		testCrate2.addCrateToCrate(testCrate);
		showroom.update();
		items = showroom.getItems();
		System.out.println("Showroom items:");
		for (Settings i : items.keySet()) {
			System.out.println("Item "+i.toString()+" :  "+items.get(i));
		}
		
		
	}

}
