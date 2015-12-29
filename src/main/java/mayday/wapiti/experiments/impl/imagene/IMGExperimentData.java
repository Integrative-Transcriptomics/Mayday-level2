package mayday.wapiti.experiments.impl.imagene;

import java.util.HashMap;

import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.wapiti.experiments.impl.microarray.ArrayLayout;
import mayday.wapiti.experiments.impl.microarray.MicroarrayExperimentData;

public class IMGExperimentData extends MicroarrayExperimentData  {

	public static final HashMap<String, Double> FLAGTYPES = new HashMap<String, Double>();
	
	static {		
		FLAGTYPES.put("Good",0d);
		FLAGTYPES.put("Poor",1d);
		FLAGTYPES.put("Empty",2d);
		FLAGTYPES.put("Negative",3d);
	}

	public IMGExperimentData(ArrayLayout layout, AbstractVector... data) {
		super(layout, data);
	}

	@Override
	protected HashMap<String, Double> flagTypes() {
		return FLAGTYPES;
	}
	

}
