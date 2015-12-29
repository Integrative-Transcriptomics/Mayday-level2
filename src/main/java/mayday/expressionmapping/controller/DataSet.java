package mayday.expressionmapping.controller;

import mayday.expressionmapping.gnu_trove_adapter.TIntArrayList;
import mayday.expressionmapping.model.geometry.DataPoint;
import mayday.expressionmapping.model.geometry.ClusterPoint;
import mayday.expressionmapping.model.geometry.container.PointList;


import java.io.Serializable;
import java.util.List;
import mayday.core.MasterTable;
import mayday.core.ProbeList;


/**
 * The data set for one session containing all nescessary options, the data sources, and the data itself.. 
 * @author Stephan Gade <stephan.gade@googlemail.com>
 */
public class DataSet implements Serializable{
	
	private static final long serialVersionUID = 4070171010209171586L;

	/**
	 * The master table contains the experiment names we show during the grouping. That helps the user
	 * decide which experiments should be grouped together.
	 */
	public MasterTable masterTable;

	/**
	 * A list of ProbeList makes no sense for ExpressionMapping, since we can only
	 * display one expression matrix at a time
	 *
	 * public List<ProbeList> probeLists;
	 * In the ProbeList probes the expression values of the probes across the experiments are stored.
	 * So this is our actual expression matrix
	 */
	public ProbeList probes;

	/**
	 * The list of group names
	 */
	public List<String> groupNames;

	/**
	 * The list of group mapping. It contains so many entries as we have groups (min. 2, max. 4).
	 * Each entry is a list of indices of experiments. We use these indices to combine the according expression
	 * values of one probe to a group expression value.
	 */
	public List<TIntArrayList> groupMappings;

	/**
	 * This is the group mode, either mean or median.
	 */
	public int groupMode;

	/**
	 * The list with the data points.
	 */
	public PointList<DataPoint> points;
	
	/**
	 * The list containing the cluster points in case the data have been clustered.
	 */
	public PointList<ClusterPoint> clusters;
	
	/**
	 * The list with the annotations belonging to the probes.
	 */
	public List<String> annotations;
	

	/*
	 * These are the frequencies of the attractor areas
	 */
	/*//	public float[] dataMainAttractorFreqs;
	//	public float[] dataAttractorFreqs;
	//	public float[] clusterMainAttractorFreqs;
	//	public float[] clusterAttractorFreqs;*/

}
