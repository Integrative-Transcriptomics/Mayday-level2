package mayday.Reveal.gui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import mayday.Reveal.actions.snplist.SNVListPlugin;
import mayday.Reveal.data.ProjectHandler;

@SuppressWarnings("serial")
public class SNPListPopupMenu extends RevealPopupMenu {
	
	public static final String NONE_CATEGORY = "";
	public static final String STATISTICS_CATEGORY = "Statistics";
	public static final String MANIPULATION_CATEGORY = "Manipulation";
	public static final String IO_CATEGORY = "IO";
	
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
		
		String category = plugin.getPopupMenuCategroy();
		insertInto(this, item, category);
	}
}
