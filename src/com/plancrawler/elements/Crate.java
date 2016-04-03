package com.plancrawler.elements;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.plancrawler.utilities.MyPoint;

public class Crate {

	private final String imagePath = "res/wooden-crate.png";
	private ArrayList<Item> items;
	private BufferedImage crateImage;
	private MyPoint location;
	private int page;
	
	public Crate(){
		this.items = new ArrayList<Item>();
		this.crateImage = readInImage();
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
	
	public boolean isInCrate(Item item) {
		return items.contains(item);
	}
	
	public boolean isInCrate(String name) {
		boolean answer = false;
		for (Item i : items)
			if (i.getName().equals(name))
				answer = true;
		return answer;
	}
	
	public Crate clone() {
		Crate newCrate = new Crate();
		for (Item i : items) {
			newCrate.addItemToCrate(i);
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

}
