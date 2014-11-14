package mayday.GWAS.settings;

import java.util.Collection;
import java.util.Set;
import java.util.TreeMap;

import mayday.GWAS.visualizations.RevealVisualization;

public class ToolbarSetting {

	private TreeMap<String, RevealVisualization> visInToolbar;
	
	public ToolbarSetting() {
		this.visInToolbar = new TreeMap<String, RevealVisualization>();
	}
	
	public void addToToolBar(RevealVisualization vis) {
		this.visInToolbar.put(vis.getName(), vis);
	}
	
	public Collection<RevealVisualization> getAllVisInToolbar() {
		return this.visInToolbar.values();
	}
	
	public Set<String> getAllVisNamesInToolbar() {
		return this.visInToolbar.keySet();
	}
}
