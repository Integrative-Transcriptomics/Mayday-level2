package mayday.tiala.multi.data.container;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mayday.core.DataSet;
import mayday.tiala.multi.data.TimepointDataSet;

/**
 * @author jaeger
 *
 */
@SuppressWarnings("serial")
public class TimepointDataSets extends ArrayList<TimepointDataSet> {
	
	/**
	 * @param datasets
	 */
	public TimepointDataSets(List<DataSet> datasets) {
		for(int i = 0; i < datasets.size(); i++)
			add(new TimepointDataSet(datasets.get(i)));	
	}
	
	/**
	 * @param size
	 */
	public TimepointDataSets(int size) {
		super(size);
	}

	/**
	 * @return set of all time points from all time point data sets
	 */
	public Double[] getAllTimePoints() {
		Set<Double> timepoints = new HashSet<Double>();
		for(TimepointDataSet ds : this) {
			timepoints.addAll(ds);
		}
		return timepoints.toArray(new Double[0]);
	}
}
