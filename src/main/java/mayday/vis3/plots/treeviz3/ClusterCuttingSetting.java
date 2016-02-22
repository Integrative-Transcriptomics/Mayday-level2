package mayday.vis3.plots.treeviz3;

import java.awt.Color;

import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.ColorSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.core.settings.typed.StringSetting;

/**
 * @author Eugen Netz
 */
public class ClusterCuttingSetting extends HierarchicalSetting {

	private BooleanSetting useClusterCutting;
	private BooleanSetting addPath;
	private boolean addPathState;
	private HierarchicalSetting plotSelection2;
	private BooleanSetting multiProfilePlot;
	private BooleanSetting silhouettePlot;
	private Boolean[] chosenPlots;
	private RestrictedStringSetting lineOrPath;
	private ColorSetting lineColor;
	private BooleanSetting acceptClustering;
	private StringSetting clusteringName;
	private StringSetting clusterPrefix;
	
	public ClusterCuttingSetting(String Name) {
		super(Name);
		addSetting(useClusterCutting = new BooleanSetting("Use Cluster Cutting", null, true));
		addSetting(addPath = new BooleanSetting("Use multiple cutting paths", null, false));
		addPathState = false;
		
		addSetting(clusteringName = new StringSetting("Clustering Name", null, ""));
		addSetting(clusterPrefix = new StringSetting("Cluster Prefix", null, "Cut Cluster"));
		addSetting(plotSelection2 = new HierarchicalSetting("Select Plots"));
		plotSelection2.addSetting(multiProfilePlot = new BooleanSetting("Multi Profile Plot", null, true));
		plotSelection2.addSetting(silhouettePlot = new BooleanSetting("Silhouette Plot", null, false));
		this.chosenPlots = new Boolean[2];
		this.chosenPlots[0] = true;
		this.chosenPlots[1] = false;
		
		String[] optionsLineOrPath = {"Path", "Line"};
		addSetting(lineOrPath = new RestrictedStringSetting("Line or path", null, 1, optionsLineOrPath));
		addSetting(lineColor = new ColorSetting("Line/Path color", null, Color.blue));
		addSetting(acceptClustering = new BooleanSetting("Accept current Clustering", null, false));
	}

	public boolean getApplyClusterCutting() {
		return useClusterCutting.getBooleanValue();
	}
	
	public boolean getAddPath() {
		return addPath.getBooleanValue();
	}
	
	public boolean addPathStateChanged() {
		return !(addPathState == addPath.getBooleanValue());
	}
	
	public void setAddPathState() {
		addPathState = addPath.getBooleanValue();
	}
	
	public String getClusteringName() {
		return clusteringName.getStringValue();
	}
	
	public void setClusteringName(String name) {
		clusteringName.setStringValue(name);
	}
	
	public String getClusterPrefix() {
		return clusterPrefix.getStringValue();
	}
	
	public boolean getMultiProfilePlot() {
		return multiProfilePlot.getBooleanValue();
	}
	
	public void setMultiProfilePlot(boolean value) {
		this.multiProfilePlot.setBooleanValue(value);
	}
	
	public boolean getSilhouettePlot() {
		return silhouettePlot.getBooleanValue();
	}
	
	public void setSilhouettePlot(boolean value) {
		this.silhouettePlot.setBooleanValue(value);
	}

	public String getLineOrPath() {
		return lineOrPath.getStringValue();
	}

	public Color getLineColor() {
		return lineColor.getColorValue();
	}
	
	public boolean getAcceptClustering() {
		return acceptClustering.getBooleanValue();
	}
	
	public void resetAcceptClustering() {
		acceptClustering.setBooleanValue(false);
	}
	
	public void setChosenPlots() {
		this.chosenPlots[0] = getMultiProfilePlot();
		this.chosenPlots[1] = getSilhouettePlot();
	}
	
	public boolean multiProfilePlotChanged() {
		return (chosenPlots[0] != getMultiProfilePlot());
				
	}
	
	public boolean silhouettePlotChanged() {
		return (chosenPlots[1] != getSilhouettePlot());
	}
	
	@Override
	public ClusterCuttingSetting clone() {
		ClusterCuttingSetting ccs = new ClusterCuttingSetting(getName());
		ccs.fromPrefNode(this.toPrefNode());
		return ccs;
	}
	
	public void toggleUseClusterCutting() {
		this.useClusterCutting.setBooleanValue(!this.useClusterCutting.getBooleanValue());
	}
	
	public void toggleAddPath() {
		this.addPath.setBooleanValue(!this.addPath.getBooleanValue());
	}
	
	public void toggleAcceptClustering() {
		this.acceptClustering.setBooleanValue(!this.acceptClustering.getBooleanValue());
	}

}
