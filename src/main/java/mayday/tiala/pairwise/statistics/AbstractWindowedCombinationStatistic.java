package mayday.tiala.pairwise.statistics;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import mayday.core.math.pcorrection.PCorrectionPlugin;
import mayday.core.math.pcorrection.methods.None;
import mayday.core.settings.Settings;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.methods.PCorrectionMethodSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.tiala.pairwise.data.probes.StatisticsProbe;

public abstract class AbstractWindowedCombinationStatistic extends AbstractCombinationStatistic implements SettingChangeListener {

	protected IntSetting windowSize;
	protected PCorrectionMethodSetting correctPValue;
	
	protected int winmin, winmax;
	protected int[] windowstarts = new int[0];
	
	public double[] computeStatisticForProbe(StatisticsProbe p) {
		return computeStatisticFromVectors(p.getMappedSourceValues());
	}
	
	public double[] computeStatisticFromVectors(double[][] source) {
		double[] ret = new double[windowstarts.length];
		for (int i=0; i!=ret.length; ++i) {
			ret[i] = computeStatisticForWindow(source, windowstarts[i]); 
		}
		return ret; 
	}
	
	public double computeStatisticForWindow(double[][] source, int windowstart) {
		int winsize = windowSize.getIntValue();
		double[] v1 = new double[winsize];
		double[] v2 = new double[winsize];
		System.arraycopy(source[0], windowstart, v1, 0, winsize);
		System.arraycopy(source[1], windowstart, v2, 0, winsize);
		return computeStatisticsForWindowedVectors(v1,v2);
	}
	
	public abstract double computeStatisticsForWindowedVectors(double[] win1, double[] win2);


	public int getOutputDimension() {
		return windowstarts.length;
	}

	public List<String> getOutputNames(List<String> inputNames) {
		LinkedList<String> expNames = new LinkedList<String>();
		String wstart, wend;		
		for (int wi : windowstarts) {
			wstart = inputNames.get(wi);
			wend = inputNames.get(wi+windowSize.getIntValue()-1);
			expNames.add(wstart+" - "+wend);
		}
		return expNames;
	}


	public void initStatistics(int numberOfProbes, int numberOfExperiments) {
		if (nop != numberOfProbes || noe != numberOfExperiments) {
			invalidateCurrent();
			nop = numberOfProbes;
			noe = numberOfExperiments;
			winmin=1;
			winmax=numberOfExperiments;
			windowSize.setRange(winmin,winmax,true,true);
			prepareWindows();	
		}
	}

	protected void prepareWindows() {
		int winsize = windowSize.getIntValue();
		int window_middle = winsize/2;
		int wloss = (winsize/2)*2;
		windowstarts = new int[ noe-wloss ];
		int i=0;
		for (int windowpos = 0; windowpos!=noe; ++windowpos) {
			int window_start = windowpos - window_middle;
			int window_end = windowpos + window_middle;
			if (window_start>=0 && window_end<noe) {
				windowstarts[i++] = window_start;
			}
		}
	}
	
	public void applyStatistic() {
		for (StatisticsProbe p : probes ) {
			if (p.getStatisticHash()!=currentHash || p.getValues()==null) {
				p.setValuesFromStatistic(computeStatisticForProbe(p));
				p.setStatisticHash(currentHash);
			}
		}
		List<double[]> pv = new ArrayList<double[]>();
		for (StatisticsProbe pb : probes) {
			pv.add(pb.getValues());
		}
		PCorrectionPlugin pcorr = correctPValue.getInstance();
		pv = pcorr.correct(pv);
		int i=0;
		for (StatisticsProbe pb : probes) {
			pb.setValuesFromStatistic(pv.get(i));
			++i;
		}
	}
	
	public Settings getSettings() {
		if (settings==null) {
			settings = new Settings(new HierarchicalSetting("Windowed t test"), null);
			windowSize = new IntSetting("Window size","The size of the window to slide over the experiments",3,winmin,winmax,true,true);
			settings.getRoot().addSetting(windowSize);
			windowSize.addChangeListener(this);
			correctPValue = new PCorrectionMethodSetting("P-value correction method",null,new None());
			settings.getRoot().addSetting(correctPValue);
			correctPValue.addChangeListener(this);
		}
		return settings;
	}
	
	public void stateChanged(SettingChangeEvent e) {
		invalidateCurrent();
		prepareWindows();
		store.statisticChanged();
	}
}
