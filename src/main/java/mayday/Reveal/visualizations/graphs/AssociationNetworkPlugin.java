package mayday.Reveal.visualizations.graphs;

import mayday.Reveal.data.meta.MetaInformation;
import mayday.Reveal.data.meta.SLResults;
import mayday.Reveal.data.meta.TLResults;
import mayday.Reveal.utilities.RevealMenuConstants;
import mayday.Reveal.visualizations.RevealVisualization;
import mayday.Reveal.visualizations.RevealVisualizationPlugin;
import mayday.Reveal.visualizations.graphs.SingleLocusNetwork.SLN;
import mayday.Reveal.visualizations.graphs.TwoLocusNetwork.TLN;

public class AssociationNetworkPlugin extends RevealVisualizationPlugin {

	@Override
	public String getType() {
		return "vis.AN";
	}

	@Override
	public String getDescription() {
		return "eQTL Association Network";
	}

	@Override
	public String getName() {
		return "eQTL Association Network";
	}

	@Override
	public String getIconPath() {
		return "mayday/GWAS/icons/plots/eQTL-AN.png";
	}

	@Override
	public String getMenuName() {
		return "eQTL Association Network";
	}

	@Override
	public boolean usesScrollPane() {
		return true;
	}

	@Override
	public RevealVisualization getComponent() {
		MetaInformation mi = projectHandler.getSelectedMetaInformation();
		
		if(mi instanceof SLResults) {
			return new SLN(projectHandler);
		} else if(mi instanceof TLResults) {
			return new TLN(projectHandler);
		}
		
		return new SLN(projectHandler);
	}

	@Override
	public String getMenu() {
		return RevealMenuConstants.VIS_MENU_NETWORK_SUBMENU;
	}

	@Override
	public boolean showInToolbar() {
		return true;
	}

	@Override
	public boolean usesViewSetting() {
		return true;
	}
}
