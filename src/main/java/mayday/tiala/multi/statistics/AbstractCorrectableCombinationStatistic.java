package mayday.tiala.multi.statistics;

import java.util.ArrayList;
import java.util.List;

import mayday.core.math.pcorrection.methods.None;
import mayday.core.settings.Settings;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.methods.PCorrectionMethodSetting;
import mayday.tiala.multi.data.probes.StatisticsProbe;

/**
 * @author jaeger
 */
public abstract class AbstractCorrectableCombinationStatistic extends AbstractCombinationStatistic implements SettingChangeListener {

	protected PCorrectionMethodSetting correctPValue;
	
	/**
	 * Constructor
	 */
	public AbstractCorrectableCombinationStatistic() {
		isStatistic=true;
	}
	
	public void applyStatistic() {
		getSettings();
		invalidateCurrent(); // all probes HAVE to be computed every time because of corrections
		super.applyStatistic();		
		List<Double> pv = new ArrayList<Double>();
		for (StatisticsProbe pb : probes) {
			pv.add(pb.getValue(0));
		}
		pv = correctPValue.getInstance().correct(pv);
		int i=0;
		for (StatisticsProbe pb : probes) {
			pb.setValuesFromStatistic(new double[]{pv.get(i)});
			++i;
		}
	}

	public Settings getSettings() {
		if (settings==null) {
			correctPValue = new PCorrectionMethodSetting("P-value correction method",null,new None());
			settings = new Settings(new HierarchicalSetting(getName()).addSetting(correctPValue), null);
			correctPValue.addChangeListener(this);
		}
		return settings;
	}
	
	public int getOutputDimension() {
		return 1;
	}

	/**
	 * @return name
	 */
	public abstract String getName();
	
	public void stateChanged(SettingChangeEvent e) {
		invalidateCurrent();
		store.statisticChanged();
	}
}
