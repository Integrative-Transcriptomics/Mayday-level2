package mayday.tiala.pairwise.statistics;

import java.util.HashMap;
import java.util.Map;

import mayday.core.ClassSelectionModel;
import mayday.core.math.stattest.StatTestPlugin;
import mayday.core.math.stattest.StatTestResult;
import mayday.core.meta.types.DoubleMIO;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Settings;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.methods.StatTestSetting;
import mayday.statistics.TTest.TTestPlugin;

public class WindowedStatTest extends AbstractWindowedCombinationStatistic {

	protected Map<Object, double[]> dummy = new HashMap<Object, double[]>();
	protected Object key = new Integer(0);
	protected ClassSelectionModel csm;
	protected StatTestSetting testSetting;
	
	public void initStatistics(int nop, int noe) {
		super.initStatistics(nop, noe);
		csm = null;
	}
	
	public void prepareWindows() {
		super.prepareWindows();
		csm = null;
	}
	
	public void applyStatistic() {
		getSettings();
		super.applyStatistic();
	}
	
	
	
	public double computeStatisticsForWindowedVectors(double[] win1, double[] win2) {
		try {
			StatTestPlugin test = testSetting.getInstance();
			
			if (csm==null) {
				csm = new ClassSelectionModel(win1.length+win2.length,2);
				for (int i=0; i!=win1.length; ++i) 
					csm.setClass(i, "0");
				for (int i=0; i!=win2.length; ++i) 
					csm.setClass(i+win1.length, "1");
			}			
			
			double[] arr = new double[win1.length+win2.length];
			System.arraycopy(win1, 0, arr, 0, win1.length);
			System.arraycopy(win2, 0, arr, win1.length, win2.length);			
			dummy.put(key, arr);
			
			StatTestResult str = test.runTest(dummy, csm);
			return ((DoubleMIO)str.getPValues().getMIO(key)).getValue();
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			return Double.NaN;
		}
	}
	
	public Settings getSettings() {
		if (settings==null) {
			super.getSettings(); 
			// now correctPValue!=null
			testSetting = new StatTestSetting("Statistical Test", null, new TTestPlugin());
			HierarchicalSetting hs = new HierarchicalSetting(getName())
					.addSetting(windowSize)
					.addSetting(testSetting)
					.addSetting(correctPValue);
			hs.addChangeListener(this);
			settings = new Settings(hs , null);
		}
		
		return settings;
	}

	public String getName() {
		return "Windowed t test";
	}
	
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(	
				getClass(), 
				"PAS.tentakelstat.windowedttest", 
				null,
				MC, 
				null,
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Computes a p value for windows over the vectors",
				"Windowed statistical test");				
	}
	
	public WindowedStatTest(){
		isStatistic=true;
	}


}
