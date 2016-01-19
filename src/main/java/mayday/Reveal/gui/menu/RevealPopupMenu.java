package mayday.Reveal.gui.menu;

import java.awt.Component;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import mayday.Reveal.data.ProjectHandler;

@SuppressWarnings("serial")
public class RevealPopupMenu extends JPopupMenu {

	protected ProjectHandler projectHandler;
	
	public RevealPopupMenu(ProjectHandler projectHandler) {
		super();
		this.projectHandler = projectHandler;
	}
	
	private void insert(JPopupMenu menu, JMenuItem item, String category) {
		Component[] comps = menu.getComponents();
		
		if(category == SNPListPopupMenu.NONE_CATEGORY) {
			for(int i = 0; i < comps.length; i++) {
				String name;
				if(comps[i] instanceof JMenu) {
					name = ((JMenu)comps[i]).getText();
				} else {
					name = ((JMenuItem)comps[i]).getText();
				}
				
				String itemText = item.getText();
				if(itemText.compareTo(name) <= 0) {
					menu.add(item, i);
					return;
				}
			}
			menu.add(item);
			return;
		} else {
			for(int i = 0; i < comps.length; i++) {
				if(comps[i] instanceof JMenu) {
					JMenu m = (JMenu)comps[i];
					if(m.getText().equals(category)) {
						Component[] menuItems = m.getMenuComponents();
						for(int j = 0; j < menuItems.length; j++) {
							JMenuItem c = (JMenuItem)menuItems[j];
							String name = c.getText();
							String itemText = item.getText();
							if(itemText.compareTo(name) <= 0) {
								m.add(item, j);
								return;
							}
						}
						m.add(item);
						return;
					}
				}
			}
			
			JMenu m = new JMenu(category);
			m.add(item);
			
			for(int i = 0; i < comps.length; i++) {
				String name;
				if(comps[i] instanceof JMenu) {
					name = ((JMenu)comps[i]).getText();
				} else {
					name = ((JMenuItem)comps[i]).getText();
				}
				
				String mText = m.getText();
				if(mText.compareTo(name) <= 0) {
					menu.add(m, i);
					return;
				}
			}
			
			menu.add(m);
		}
	}
	
	protected void insertInto(JPopupMenu menu, JMenuItem item, String category) {
		this.insert(menu, item, category);
	}
}
