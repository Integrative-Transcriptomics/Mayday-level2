package it.genomering.gui.actions;

import it.genomering.structure.Genome;
import it.genomering.structure.SuperGenome;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;

import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.SelectableHierarchicalSetting;
import mayday.core.settings.typed.StringMapSetting;
import mayday.wapiti.containers.identifiermapping.IdentifierMap;
import mayday.wapiti.containers.identifiermapping.IdentifierMapSetting;

public class ChangeGenomeNamesAction extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1242674222005669731L;

	protected GenomeNameSetting setting;
	protected SuperGenome superGenome;
	
	public ChangeGenomeNamesAction(SuperGenome superGenome) {
		super("Change Genome Names");
		this.superGenome = superGenome;
		setting = new GenomeNameSetting(superGenome);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		SettingDialog sd = new SettingDialog(null, setting.getName(), setting);
		sd.showAsInputDialog();
		
		if(sd.closedWithOK()) {
			IdentifierMap idm = setting.getIdentifierMap();
			
			if (idm==null)
				return;
			
			List<Genome> genomes = superGenome.getGenomes();
			
			for (Genome g : genomes) {
				String curName = g.getName();
				String newName = idm.map(curName);
				if (!newName.equals(curName)) {
					g.setName(newName);
				}
			}
			
			superGenome.genomeNamesChanged();
		}
	}
	
	private class GenomeNameSetting extends HierarchicalSetting {
		
		private SelectableHierarchicalSetting chooseMethodSetting;
		
		private SuperGenome sg;
		
		private IdentifierMapSetting mapFromFile;
		private GenomeNameEditSetting editNames;
		
		private HierarchicalSetting[] predef;
		
		public GenomeNameSetting(SuperGenome sg) {
			super("Change Genome Names");
			this.sg = sg;

			mapFromFile = new IdentifierMapSetting();
			editNames = new GenomeNameEditSetting(sg);
			
			predef = new HierarchicalSetting[]{mapFromFile, editNames};
			
			chooseMethodSetting = new SelectableHierarchicalSetting("Choose Mapping Method", null, 0, predef);

			addSetting(chooseMethodSetting);
		}
		
		public IdentifierMap getIdentifierMap() {
			int selectedIndex = chooseMethodSetting.getSelectedIndex();
			switch(selectedIndex) {
			case 0:
				return mapFromFile.getIdentifierMap();
			case 1:
				return editNames.getIdentifierMap();
			default:
				return editNames.getIdentifierMap();
			}
		}
		
		public GenomeNameSetting clone() {
			GenomeNameSetting s = new GenomeNameSetting(sg);
			s.fromPrefNode(GenomeNameSetting.this.toPrefNode());
			return s;
		}
	}
	
	private class GenomeNameEditSetting extends HierarchicalSetting {

		private SuperGenome sg;
		private StringMapSetting nameMappingSetting;
		
		public GenomeNameEditSetting(SuperGenome sg) {
			super("Manual Name Editting");
			this.sg = sg;
			
			List<Genome> genomes = sg.getGenomes();
			Map<String, String> nameMapping = new HashMap<String, String>();
			
			for(Genome g : genomes) {
				nameMapping.put(g.getName(), g.getName());
			}
			
			nameMappingSetting = new StringMapSetting("Map Names", null, nameMapping);
			
			addSetting(nameMappingSetting);
		}
		
		public IdentifierMap getIdentifierMap() {
			IdentifierMap map = new IdentifierMap("Manual Genome Names");
			for(Genome g : sg.getGenomes())
				map.put(g.getName(), nameMappingSetting.getStringMapValue().get(g.getName()));
			return map;
		}
		
		public GenomeNameEditSetting clone() {
			GenomeNameEditSetting s = new GenomeNameEditSetting(sg);
			s.fromPrefNode(GenomeNameEditSetting.this.toPrefNode());
			return s;
		}
	}
}
