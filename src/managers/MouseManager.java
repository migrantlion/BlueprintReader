package managers;

import java.util.HashMap;

public class MouseManager {

	private static HashMap<String, Integer> keys = new HashMap<String, Integer>();
	private static HashMap<Integer, Boolean> active = new HashMap<Integer, Boolean>();
	private static int nextKey = 0;

	public static int register(String name) {
		int key = nextKey;
		keys.put(name, key);
		active.put(key, false);
		nextKey += 1;

		return key;
	}

	public static boolean unregister(String name) {
		if (keys.get(name) == null)
			return false;
		int keyval = getKey(name);
		active.remove(keyval);
		return keys.remove(name, keyval);
	}

	public static int getKey(String name) {
		if (keys.get(name) == null)
			return -1;
		else
			return keys.get(name);
	}

	public static void activate(String name) {
		if (getKey(name) == -1)
			return;
		for (String key : keys.keySet()) {
			if (key == name)
				active.replace(getKey(name), true);
		}
	}
	
	public static void activateJust(String name) {
		if (getKey(name) == -1)
			return;
		for (String key : keys.keySet()) {
			if (key == name)
				active.replace(getKey(name), true);
			else
				active.replace(getKey(name), false);
		}
	}

	public static boolean deactivate(String name) {
		if (getKey(name) == -1)
			return false;
		else
			return active.replace(getKey(name), true, false);
	}

	public static String getActive() {
		int a = -1;
		for (int i : active.keySet())
			if (active.get(i))
				a = i;

		if (a == -1)
			return null;
		else {
			return findRegistrantFromKey(a);
		}
	}

	private static String findRegistrantFromKey(int k) {
		String name = null;

		for (String s : keys.keySet()) {
			if (keys.get(s) == k)
				name = s;
		}
		return name;
	}

	public static boolean isActive(String name) {
		int key = getKey(name);
		if (key == -1)
			return false;
		else
			return active.get(key);
	}
}
