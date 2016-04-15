package com.plancrawler.utilities;

import java.util.ArrayList;

import com.plancrawler.elements.Item;
import com.plancrawler.elements.Mark;

public class Helpers {

	public static ArrayList<Item> combineItemArrays(ArrayList<Item> a, ArrayList<Item> b) {
		ArrayList<Item> c = new ArrayList<Item>();

		c.addAll(a);
		for (Item bi : b) {
			if (c.contains(bi))
				for (Item cj : c) {
					if (Item.ofSameThing(bi, cj))
						for (Mark m : bi.getMarks())
							cj.insertMark(m);
				}
			else
				c.add(bi);
		}
		return c;
	}
}
