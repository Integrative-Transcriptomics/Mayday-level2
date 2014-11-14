package mayday.GWAS.visualizations;

import java.util.Collection;
import java.util.HashMap;

import mayday.GWAS.RevealPlugin;
import mayday.GWAS.data.ProjectHandler;
import mayday.GWAS.data.SNPList;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public abstract class RevealVisualizationPlugin extends RevealPlugin {
	
	public static final String CATEGORY = "Visualization";
	public static final String MC = Constants.MC_REVEAL + "/" + CATEGORY;
	
	protected ProjectHandler projectHandler;

	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				getClass(),
				getCompleteType(),
				new String[0],
				MC,
				new HashMap<String, Object>(),
				"G&uuml;nter J&auml;ger",
				"jaeger@informatik.uni-tuebingen.de",
				getDescription(),
				getName()
		);
		if(getIconPath() != null)
			pli.setIcon(getIconPath());
		if(pli.getMenuName() != null)
			pli.setMenuName(getMenuName());
		return pli;
	}

	@Override
	public void init() {}
	
	public abstract String getType();
	
	public abstract String getDescription();
	
	public abstract String getName();
	
	public abstract String getIconPath();
	
	public abstract String getMenuName();
	
	public abstract RevealVisualization getComponent();
	
	public abstract boolean showInToolbar();
	
	public void setProjectHandler(ProjectHandler projectHandler) {
		this.projectHandler = projectHandler;
	}
	
	public ProjectHandler getProjectHandler() {
		return this.projectHandler;
	}

	public abstract boolean usesScrollPane();
	
	public abstract boolean usesViewSetting();
	
	public void run(Collection<SNPList> snps) {
		return; //nothing to do here!
	}
	
	@Override
	public String getCategory() {
		return CATEGORY;
	}
}
