package mayday.wapiti.experiments.impl.microarray;

import java.util.HashMap;

import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.wapiti.experiments.generic.featureexpression.AbstractFeatureExpressionData;
import mayday.wapiti.experiments.impl.microarray.ArrayLayout;

public abstract class MicroarrayExperimentData extends AbstractFeatureExpressionData  {

	protected ArrayLayout layout; 

	/** Parameter order:
	 * red fg, red bg, green fg, green bg, flags
	 * @param data
	 */
	public MicroarrayExperimentData(ArrayLayout layout, AbstractVector... data) {
		super(data[4], null, new AbstractVector[]{data[0],data[2]}, new AbstractVector[]{data[1],data[3]}, new String[]{"Red","Green"});
		this.flagTypes = flagTypes();
		this.layout = layout;
	}
	
	public ArrayLayout getArrayLayout() {
		return layout;
	}
	
	protected abstract HashMap<String, Double> flagTypes();
	

}
