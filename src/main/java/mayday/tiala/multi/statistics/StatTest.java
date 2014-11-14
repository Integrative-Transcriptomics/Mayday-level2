package mayday.tiala.multi.statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import mayday.core.ClassSelectionModel;
import mayday.core.math.stattest.StatTestPlugin;
import mayday.core.math.stattest.StatTestResult;
import mayday.core.meta.types.DoubleMIO;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Settings;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.methods.StatTestSetting;
import mayday.statistics.TTest.TTestPlugin;
import mayday.tiala.multi.data.probes.StatisticsProbe;

/**
 * @author jaeger
 */
public class StatTest extends AbstractCorrectableCombinationStatistic implements SettingChangeListener {

	protected StatTestSetting testSetting;
	
	@SuppressWarnings("unchecked")
	public void applyStatistic() {
		getSettings();
		StatTestPlugin test = testSetting.getInstance();
		ClassSelectionModel classes = new ClassSelectionModel(2*noe,2);
		for (int i=0; i!=noe; ++i) 
			classes.setClass(i, "0");
		for (int i=0; i!=noe; ++i) 
			classes.setClass(i+noe, "1");

		Map<StatisticsProbe, double[]> input = new HashMap<StatisticsProbe, double[]>();
		for (StatisticsProbe pb : probes) {
			double[][] v = pb.getMappedSourceValues();
			double[] arr = new double[2*noe];
			System.arraycopy(v[0], 0, arr, 0, noe);
			System.arraycopy(v[1], 0, arr, noe, noe);
			input.put(pb, arr);
		}
		StatTestResult str = test.runTest((Map)input, classes);
		
		List<Double> pv = new ArrayList<Double>();
		for (StatisticsProbe pb : probes) {
			pv.add(((DoubleMIO)str.getPValues().getMIO(pb)).getValue());
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
			super.getSettings(); 
			// now correctPValue!=null
			testSetting = new StatTestSetting("Statistical Test", null, new TTestPlugin());
			testSetting.addChangeListener(this);
			HierarchicalSetting hs = new HierarchicalSetting(getName())
				.addSetting(testSetting)
				.addSetting(correctPValue);
			settings = new Settings(hs, null);
			hs.addChangeListener(this);
		}
		return settings;
	}

	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(	
				getClass(), 
				"PAS.tentakelstat.stattest2", 
				null,
				MC, 
				null,
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Computes a p value based on a statistical test",
				"Statistical Test p-value");				
	}
	
	public double[] computeStatisticFromVectors(double[][] source) {
		throw new RuntimeException("Your doing it wrong.");
	}

	public List<String> getOutputNames(List<String> inputNames) {
		LinkedList<String> lls = new LinkedList<String>();
		lls.add("p value");
		return lls;
	}

	public String getName() {
		return "Statistical Test p-value";
	}
}
