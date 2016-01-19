package mayday.Reveal;

import java.util.Set;

import mayday.Reveal.data.ProjectHandler;
import mayday.Reveal.gui.RevealGUI;
import mayday.Reveal.listeners.ProjectEventHandler;
import mayday.Reveal.visualizations.RevealVisualizationPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;

/**
 * @author jaeger
 * 
 */
public class RevealMain {

	private ProjectHandler projectHandler;
	private RevealGUI revealGUI;

	/**
	 * start reveal
	 * @param ds 
	 */
	public RevealMain() {
		//FIXME this is just for testing issues so far
		System.setProperty("sun.java2d.opengl", "True");
		
		projectHandler = new ProjectHandler();
		ProjectEventHandler peh = new ProjectEventHandler(projectHandler);
		projectHandler.setProjectEventHandler(peh);
		
		//set up pairwise comunication
		this.revealGUI = new RevealGUI(projectHandler);
		this.revealGUI.initializeComponents();
		
		projectHandler.setGUI(this.revealGUI);
		
		//set up plugins
		this.initPlugins();

		this.revealGUI.setVisible(true);
	}
	
	private void initPlugins() {
		PluginManager pm = PluginManager.getInstance();
		Set<PluginInfo> plis = pm.getPluginsFor(Constants.MC_REVEAL);
		
		for(PluginInfo pli : plis) {
			RevealPlugin plugin = (RevealPlugin) pli.getInstance();
			plugin.setProjectHandler(projectHandler);
			revealGUI.addItem(plugin);
			
		}
		
		this.initPlots(pm);
	}

	/**
	 * initialize the gui to create plots
	 */
	public void initPlots(PluginManager pm) {
		Set<PluginInfo> plis = pm.getPluginsFor(RevealVisualizationPlugin.MC);
		
		for(PluginInfo pli : plis) {
			RevealVisualizationPlugin plugin = (RevealVisualizationPlugin) pli.getInstance();
			plugin.setProjectHandler(projectHandler);
			revealGUI.addPlotItem(plugin);
		}
	}
}
