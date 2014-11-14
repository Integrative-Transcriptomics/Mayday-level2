package mayday.GWAS.gui.menu;

import java.awt.Component;
import java.util.HashMap;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class RevealMenuTree {

	private HashMap<String, Component> items;
	
	public RevealMenuTree() {
		items = new HashMap<String, Component>();
	}
	
	public boolean hasItem(String itemText) {
		return items.containsKey(itemText);
	}
	
	public void addItem(JMenu parent, Component item, String itemText) {
		insertInto(parent, item, itemText);
	}
	
	public void addRootMenu(String title, JMenu menu) {
		items.put(title, menu);
	}
	
	private void insertInto(JMenu parent, Component item, String itemText) {
		int pos = getInsertionPosition(parent, itemText);
		parent.add(item, pos);
		items.put(itemText, item);
	}
	
	private int getInsertionPosition(JMenu menu, String compName) {
		int numItems = menu.getItemCount();
		for(int i = 0; i < numItems; i++) {
			JMenuItem c = menu.getItem(i);
			String name = c.getText();
			if(compName.compareTo(name) <= 0)
				return i;
		}
		return numItems;
	}

	public Component getItem(String itemText) {
		return items.get(itemText);
	}
}
