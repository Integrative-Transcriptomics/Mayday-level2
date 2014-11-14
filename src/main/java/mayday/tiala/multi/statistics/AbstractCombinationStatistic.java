package mayday.tiala.multi.statistics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import mayday.core.DataSet;
import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.meta.MIGroup;
import mayday.core.meta.types.DoubleListMIO;
import mayday.core.meta.types.DoubleMIO;
import mayday.core.meta.types.IntegerMIO;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginManager;
import mayday.core.settings.SettingComponent;
import mayday.core.settings.Settings;
import mayday.tiala.multi.data.AlignmentStore;
import mayday.tiala.multi.data.mastertables.AlignmentDerivedMasterTable;
import mayday.tiala.multi.data.probes.StatisticsProbe;

/**
 * @author jaeger
 */
public abstract class AbstractCombinationStatistic extends AbstractPlugin implements ProbeCombinationStatistic {

	/**
	 * 
	 */
	public final static String MC = "Probe Combination Statistics (Tiala2)";
	
	protected AlignmentStore store;
	
	protected int currentHash;
	
	protected Collection<StatisticsProbe> probes;
	protected int nop =- 1, noe =- 1;
	
	protected boolean isStatistic = false;
		
	protected SettingComponent statPanel;
	
	protected Settings settings;
		
	/**
	 * @param source
	 * @return statistics vector
	 */
	public abstract double[] computeStatisticFromVectors(double[][] source);

	public void applyStatistic() {
		for (StatisticsProbe p : probes ) {
			if (p.getStatisticHash() != currentHash || p.getValues() == null) {
				p.setValuesFromStatistic(computeStatisticForProbe(p));
				p.setStatisticHash(currentHash);
			}
		}
	}
	
	public void init() { 
		currentHash = hashCode();	
	}

	
	/**
	 * @param p
	 * @return statistics vector
	 */
	public double[] computeStatisticForProbe(StatisticsProbe p) {
		return computeStatisticFromVectors(p.getMappedSourceValues());
	}
	
	public void initStatistics(int numberOfProbes, int numberOfExperiments) {
		if (nop != numberOfProbes || noe != numberOfExperiments)
			invalidateCurrent();
		nop = numberOfProbes;
		noe = numberOfExperiments;
	}
	
	public Settings getSettings() {
		return null;
	}

	public void setInput(Collection<StatisticsProbe> input) {
		if (probes!=input) {
			invalidateCurrent();
			probes = input;
		}
	}
	
	public void invalidateCurrent() {
		++currentHash;
	}
	
	public String toString() {
		return PluginManager.getInstance().getPluginFromClass(getClass()).getName();
	}
	
	public void setStore(AlignmentStore Store, int id) {
		store = Store;
	}
	
	public void createMIOs() {
		if (probes.size()>0) {
			AlignmentDerivedMasterTable mata = (AlignmentDerivedMasterTable)probes.iterator().next().getMasterTable();
			createMIOs(mata.getMasterTable(0).getDataSet(), mata);
			createMIOs(mata.getMasterTable(1).getDataSet(), mata);
		}		
	}
	
	protected void createMIOs(DataSet targetDS, MasterTable mata) {
		boolean isVector = getOutputDimension()>1;
		
		String valueGroupType = isVector?"PAS.MIO.DoubleList":"PAS.MIO.Double";		
		MIGroup valueGroup = targetDS.getMIManager().newGroup(valueGroupType, toString(), "/Combination Statistics");
		MIGroup runGroup=null, countGroup=null;
		if (isStatistic && isVector) {
			runGroup = targetDS.getMIManager().newGroup("PAS.MIO.Integer", "longest run below .05", valueGroup);
			countGroup = targetDS.getMIManager().newGroup("PAS.MIO.Integer", "positions below .05", valueGroup);
		}
		for (Probe pb : mata.getProbes().values()) {
			StatisticsProbe spb = (StatisticsProbe)pb;
			ArrayList<Double> adb = new ArrayList<Double>();
			int sum=0, run=0, mrun=0;
			for (double d : spb.getValues()) {
				adb.add(d);
				if (d<0.05) { 
					++sum;
					++run;
					if (run>mrun)
						mrun=run;
				} else {
					run=0;
				}
			}
			Probe target = targetDS.getMasterTable().getProbe(spb.getSourceName());
			
			if (isVector) {
				((DoubleListMIO)valueGroup.add(target)).setValue(adb);
			} else {
				((DoubleMIO)valueGroup.add(target)).setValue(adb.get(0));
			}
			if (runGroup!=null &&countGroup!=null) {
				((IntegerMIO)runGroup.add(target)).setValue(mrun);
				((IntegerMIO)countGroup.add(target)).setValue(sum);
			}
		}
	}
	

	public int compareTo(Object o) {
		return this.toString().compareTo(o.toString());
	}

	public int getOutputDimension() {
		return noe;
	}

	public List<String> getOutputNames(List<String> inputNames) {
		return inputNames;
	}

	public boolean hasSettings() {
		getSettings();
		return settings!=null;
	}
}
