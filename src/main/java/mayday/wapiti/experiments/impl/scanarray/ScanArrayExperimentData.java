package mayday.wapiti.experiments.impl.scanarray;

import java.util.HashMap;

import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.wapiti.experiments.impl.microarray.ArrayLayout;
import mayday.wapiti.experiments.impl.microarray.MicroarrayExperimentData;

public class ScanArrayExperimentData extends MicroarrayExperimentData  {

	public static final HashMap<String, Double> FLAGTYPES = new HashMap<String, Double>();
	// TODO find out about scanarray flag types
	
	/** Parameter order:
	 * red fg, red bg, green fg, green bg, flags
	 * @param data
	 */
	public ScanArrayExperimentData(ArrayLayout layout, AbstractVector... data) {
		super(layout, data);
	}
	
	@Override
	protected HashMap<String, Double> flagTypes() {
		return FLAGTYPES;
	}

}
