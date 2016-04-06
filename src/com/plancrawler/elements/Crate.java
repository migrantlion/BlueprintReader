package com.plancrawler.elements;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.plancrawler.utilities.MyPoint;

public class Crate implements Serializable{

	private static final long serialVersionUID = 1L;

	private final String imagePath = "res/wooden-crate.png";
	
	private ArrayList<Item> items;
	private ArrayList<Crate> crates;
	
	private BufferedImage crateImage;
	private MyPoint location;
	private int page;
	
	private String crateName;
	
	public Crate(String name){
		this.items = new ArrayList<Item>();
		this.crateImage = readInImage();
		this.crateName = name;
		
		readInImage();
	}
	
	private BufferedImage readInImage() {
		BufferedImage image = null;
		
		try {
			image = ImageIO.read(new File(imagePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return image;
	}
	
	public void addItemToCrate(Item item) {
		items.add(item);
	}
	
	public void addCrate(Crate c) {
		crates.add(c);
	}
	
	public boolean isInCrate(Item item) {
		return items.contains(item);
	}
	
	public boolean isInCrate(String itemName) {
		boolean answer = false;
		for (Item i : items)
			if (i.getName().equals(itemName))
				answer = true;
		return answer;
	}
	
	public Crate clone() {
		Crate newCrate = new Crate(crateName);
		for (Item i : items) {
			newCrate.addItemToCrate(i);
		}
		for (Crate c : crates) {
			newCrate.addCrate(c);
		}
		return newCrate;
	}

	public MyPoint getLocation() {
		return location;
	}

	public void setLocation(MyPoint location) {
		this.location = location;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public ArrayList<Item> getItems() {
		return items;
	}

	public BufferedImage getCrateImage() {
		return crateImage;
	}

	public void removeItemByLocation(String name, MyPoint loc, int pageNum) {
		if (isInCrate(name)) {
			Item item = getItemByName(name);
			item.delMarkAt(loc, pageNum);
		}
		
	}
	
	private Item getItemByName(String name) {
		Item item = null;
		if (!isInCrate(name)) return null;
		for (Item i : items)
			if (i.getName().equals(name)) {
				item = i;
				break;
			}
		return item;
	}
}
