package mayday.wapiti.experiments.impl.genepix;

import java.util.HashMap;

import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.wapiti.experiments.impl.microarray.ArrayLayout;
import mayday.wapiti.experiments.impl.microarray.MicroarrayExperimentData;

public class GPRExperimentData extends MicroarrayExperimentData  {

	public static final HashMap<String, Double> FLAGTYPES = new HashMap<String, Double>();
	
	static {		
		FLAGTYPES.put("Good",0d);
		FLAGTYPES.put("Excellent",100d);
		FLAGTYPES.put("Not found",-50d);
		FLAGTYPES.put("Absent",-75d);
		FLAGTYPES.put("Bad",-100d);
	}

	/** Parameter order:
	 * red fg, red bg, green fg, green bg, flags
	 * @param data
	 */
	public GPRExperimentData(ArrayLayout layout, AbstractVector... data) {
		super(layout, data);
	}
	
	@Override
	protected HashMap<String, Double> flagTypes() {
		return FLAGTYPES;
	}

}
