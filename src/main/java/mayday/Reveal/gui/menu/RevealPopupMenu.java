package mayday.Reveal.gui.menu;

import java.awt.Component;

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
	
	private int getInsertionPosition(JPopupMenu menu, String compName) {
		Component[] comps = menu.getComponents();
		for(int i = 0; i < comps.length; i++) {
			JMenuItem c = (JMenuItem)comps[i];
			String name = c.getText();
			if(compName.compareTo(name) <= 0)
				return i;
		}
		return comps.length;
	}
	
	protected void insertInto(JPopupMenu menu, JMenuItem item) {
		int pos = getInsertionPosition(menu, item.getText());
		menu.add(item, pos);
	}
}
