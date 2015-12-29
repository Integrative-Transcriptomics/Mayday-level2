package mayday.wapiti.experiments.impl.affy;

import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.wapiti.experiments.generic.featureexpression.AbstractFeatureExpressionData;
import mayday.wapiti.experiments.impl.microarray.ArrayLayout;

public class CELExperimentData extends AbstractFeatureExpressionData  {

	protected ArrayLayout layout;
	
	public CELExperimentData(ArrayLayout layout, AbstractVector array, boolean[] pm) {
		super( array.subset(pm).clone() , array.subset(pm, true).clone() );
		this.layout=layout;
	}
	
	public ArrayLayout getArrayLayout() {
		return layout;
	}
	
}
