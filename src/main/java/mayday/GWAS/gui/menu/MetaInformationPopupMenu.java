package mayday.GWAS.gui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JMenuItem;

import mayday.GWAS.data.ProjectHandler;
import mayday.GWAS.data.meta.MetaInformation;
import mayday.GWAS.data.meta.manipulation.MIManipulationPlugin;

@SuppressWarnings("serial")
public class MetaInformationPopupMenu extends RevealPopupMenu {
	
	public MetaInformationPopupMenu(ProjectHandler projectHandler) {
		super(projectHandler);
	}
	
	public void addMenuItem(final MIManipulationPlugin plugin) {
		JMenuItem item = new JMenuItem(plugin.getMenuName());
		item.getAccessibleContext().setAccessibleDescription(plugin.getDescription());
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				ArrayList<MetaInformation> mis = new ArrayList<MetaInformation>();
				mis.add(projectHandler.getSelectedMetaInformation());
				plugin.runManipulation(mis);
			}
		});
		
		insertInto(this, item);
	}
}
