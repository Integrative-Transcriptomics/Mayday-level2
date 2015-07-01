package mayday.Reveal.gui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import mayday.Reveal.actions.snplist.SNVListPlugin;
import mayday.Reveal.data.ProjectHandler;

@SuppressWarnings("serial")
public class SNPListPopupMenu extends RevealPopupMenu {
	
	public SNPListPopupMenu(ProjectHandler projectHandler) {
		super(projectHandler);
	}

	public void addMenuItem(final SNVListPlugin plugin) {
		JMenuItem item = new JMenuItem(plugin.getMenuName());
		item.getAccessibleContext().setAccessibleDescription(plugin.getDescription());
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				plugin.run(projectHandler.getSelectedSNVLists());
			}
		});
		insertInto(this, item);
	}
}
