package mayday.Reveal.actions;

import java.util.Collection;

import javax.swing.JOptionPane;

import mayday.Reveal.RevealPlugin;
import mayday.Reveal.data.SNVList;
import mayday.Reveal.gui.genome.GenomeNameMappingDialog;
import mayday.Reveal.utilities.RevealMenuConstants;

public class MapGenomeNamesAction extends RevealPlugin {
	
	@Override
	public String getName() {
		return "Map Genome Sequence Names";
	}

	@Override
	public String getType() {
		return "project.genomeSeqNameMapping";
	}

	@Override
	public String getDescription() {
		return "Maps user defined genome sequence names to original sequence names";
	}

	@Override
	public String getMenuName() {
		return "Map Genome Sequence Names";
	}

	@Override
	public void run(Collection<SNVList> snpLists) {
		if(this.projectHandler.getSelectedProject() == null) {
			JOptionPane.showMessageDialog(null, "No project has been selected!");
			return;
		}
		
		if(projectHandler.getSelectedProject().getGenome() == null) {
			JOptionPane.showMessageDialog(null, "No genome is loaded for the current project!");
			return;
		}
		
		GenomeNameMappingDialog dialog = new GenomeNameMappingDialog(projectHandler);
		dialog.setVisible(true);
	}

	@Override
	public String getMenu() {
		return RevealMenuConstants.META_INFORMATION+"/Genome";
	}

	@Override
	public String getCategory() {
		return "Project/Meta-Information";
	}
}
