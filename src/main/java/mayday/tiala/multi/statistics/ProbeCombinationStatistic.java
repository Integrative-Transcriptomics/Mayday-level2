package mayday.tiala.multi.statistics;

import java.util.Collection;
import java.util.List;

import mayday.core.settings.Settings;
import mayday.tiala.multi.data.AlignmentStore;
import mayday.tiala.multi.data.probes.StatisticsProbe;

/**
 * @author jaeger
 */
@SuppressWarnings("unchecked")
public interface ProbeCombinationStatistic extends Comparable {

	/** 
	 * Initialize the statistics function for the size of the input 
	 * @param numberOfProbes 
	 * @param numberOfExperiments 
	 */ 
	public void initStatistics(int numberOfProbes, int numberOfExperiments);
	
	/** 
	 * Provide the actual input probes, but don't compute anything yet 
	 * @param input 
	 */
	public void setInput(Collection<StatisticsProbe> input);
	
	/** Returns the number of dimensions of the output (n.of. experiments) 
	 * @return output dimension
	 */
	public int getOutputDimension();
	
	/** 
	 * Returns the names of dimensions of the output, of the same number as given by getOutputDimension() 
	 * @param inputNames 
	 * @return list of output names
	 */
	public List<String> getOutputNames(List<String> inputNames);
	
	/** 
	 * Compute statistics for all probes, but don't recompute if it isn't necessary 
	 */
	public void applyStatistic();
	
	/** 
	 * Mark ALL probes for recomputation in the next call to applyStatistic() 
	 */
	public void invalidateCurrent();
	
	/** 
	 * Return the name of the statistic 
	 */
	public String toString();
	
	/** 
	 * @return a JPanel to change settings of the statistic. Only create the panel ONCE 
	 */
	public Settings getSettings();

	/**
	 * @return true if this statistic has settings that are configurable. See getSettingsPanel() 
	 */
	public boolean hasSettings();
	
	/** 
	 * connect this statistics instance to an alignment-store. The store will be notified if recomputation is needed 
	 * @param Store 
	 * @param id 
	 */
	public void setStore(AlignmentStore Store, int id);
	
	/** 
	 * Add MIOs to all input probes containing the statistic's output 
	 */
	public void createMIOs();
}
