package mayday.GWAS;

import java.util.Collection;
import java.util.HashMap;

import mayday.GWAS.data.ProjectHandler;
import mayday.GWAS.data.SNPList;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public abstract class RevealPlugin extends AbstractPlugin {

	public static final String MC = Constants.MC_REVEAL;
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
		if(getCategory() != null)
			pli.addCategory(getCategory());
		if(getMenuName() != null)
			pli.setMenuName(getMenuName());
		return pli;
	}

	@Override
	public void init() {}
	
	public abstract String getName();
	
	public abstract String getType();
	
	public abstract String getDescription();
	
	public abstract String getMenuName();
	
	public void setProjectHandler(ProjectHandler projectHandler) {
		this.projectHandler = projectHandler;
	}
	
	public ProjectHandler getProjectHandler() {
		return this.projectHandler;
	}
	
	public abstract void run(Collection<SNPList> snpLists);
	
	public abstract String getMenu();
	
	public abstract String getCategory();
	
	protected String getCompleteType() {
		return "mayday.Reveal."+getType();
	}
}
