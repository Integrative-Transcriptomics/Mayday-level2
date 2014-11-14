package mayday.tiala.multi.statistics;

import java.util.LinkedList;
import java.util.List;

import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.math.distance.measures.EuclideanDistance;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Settings;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.methods.DistanceMeasureSetting;


/**
 * @author jaeger
 */
public class Distance extends AbstractCombinationStatistic implements SettingChangeListener {

	protected DistanceMeasureSetting distanceSetting;
	protected DistanceMeasurePlugin distance;
	
	public void applyStatistic() {
		initDistance(); 
		distance = distanceSetting.getInstance();
		super.applyStatistic();
	}
	
	protected void initDistance() {
		if (distanceSetting==null)
			distanceSetting = new DistanceMeasureSetting("Distance measure", null, new EuclideanDistance());
	}
	
	public double[] computeStatisticFromVectors(double[][] source) {
		return new double[]{distance.getDistance(source[0], source[1])};
	}

	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(	
				getClass(), 
				"PAS.tentakelstat.distance2", 
				null,
				MC, 
				null,
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Computes the distance between the experiment profiles of two probes (based on a distance measure)",
				"Distance");				
	}

	public Settings getSettings() {
		if (settings==null) {
			initDistance();
			HierarchicalSetting root = new HierarchicalSetting("Distance")
			.addSetting(distanceSetting);
			root.addChangeListener(this);
			settings = new Settings(root, null);
		}
		return settings;
	}
	
	public int getOutputDimension() {
		return 1;
	}

	public List<String> getOutputNames(List<String> inputNames) {
		LinkedList<String> lls = new LinkedList<String>();
		lls.add("Distance");
		return lls;
	}

	public void stateChanged(SettingChangeEvent e) {
		invalidateCurrent();
		store.statisticChanged();
	}
}
