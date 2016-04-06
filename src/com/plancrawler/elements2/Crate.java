package com.plancrawler.elements2;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.plancrawler.utilities.MyPoint;

public class Crate implements Serializable {

	private static final long serialVersionUID = 1L;

	private ArrayList<Item> items;
	private ArrayList<Crate> crates;

	private String name;

	private MyPoint location;
	private BufferedImage image;
	private boolean isDisplay;

	public Crate(String name) {
		this.name = name;
		this.items = new ArrayList<Item>();
		this.crates = new ArrayList<Crate>();
		this.isDisplay = true;
		this.image = loadImage();
	}

	public void addItem(Item item) {
		items.add(item);
	}

	public void addCrate(Crate crate) {
		crates.add(crate);
	}

	public boolean rmItem(Item item) {
		return items.remove(item);
	}

	public boolean rmCrate(Crate crate) {
		return crates.remove(crate);
	}

	public boolean hasItemInside(Item item) {
		for (Item i : items) {
			if (i.getSettings().equals(item.getSettings()) && i.getLocation().equals(item.getLocation()))
				return true;
		}
		for (Crate c : crates) {
			if (c.hasItemInside(item))
				return true;
		}

		return false;
	}
	
	//TODO: create method to summarize item count inside crate
	

	public void emptyCrate() {
		items.clear();
		crates.clear();
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public MyPoint getLocation() {
		return location;
	}

	public void setLocation(MyPoint location) {
		this.location = location;
	}

	public boolean isDisplay() {
		return isDisplay;
	}

	public void setDisplay(boolean isDisplay) {
		this.isDisplay = isDisplay;
	}

	public ArrayList<Item> getItems() {
		return items;
	}

	public ArrayList<Crate> getCrates() {
		return crates;
	}

	public BufferedImage getImage() {
		return image;
	}

	private BufferedImage loadImage() {
		String imagePath = "res/Wooden-Crate.png";
		BufferedImage image = null;

		try {
			image = ImageIO.read(new File(imagePath));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return image;
	}
}
