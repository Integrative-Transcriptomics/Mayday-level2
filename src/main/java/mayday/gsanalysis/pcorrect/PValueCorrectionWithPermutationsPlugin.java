package mayday.gsanalysis.pcorrect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import mayday.core.math.pcorrection.ArrayBackedDoubleList;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIType;
import mayday.core.meta.types.DoubleMIO;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.structures.linalg.matrix.DoubleMatrix;

public abstract class PValueCorrectionWithPermutationsPlugin extends AbstractPlugin{
	
	public final static String MC = "Math/P-value correction methods (permutations)";
	/** Perform correction of pvalues on a vector of p-values 
	 * The rows of the matrix represent the permutations, the columns represent the items
	 * **/
	public final List<Double> correct(double[] pvalues,DoubleMatrix permutationPValues) {
		List<Double> in = new ArrayBackedDoubleList(pvalues);
		return correct(in,permutationPValues);
	}
	
	/** Perform correction of pvalues given as a MIGroup */
	public final MIGroup correct(MIGroup pvalues,DoubleMatrix permutationPValues) {
		List<Double> in = new ArrayList<Double>();
		for (Entry<Object, MIType> e : pvalues.getMIOs()) {
			in.add(((DoubleMIO)e.getValue()).getValue());
		}
		List<Double> out = correct(in,permutationPValues);
		PluginInfo dMio = PluginManager.getInstance().getPluginFromID("PAS.MIO.Double");
		MIGroup mg = new MIGroup(dMio, toString(), null);
		int i=0;
		for (Entry<Object, MIType> e : pvalues.getMIOs()) {
			((DoubleMIO)mg.add(e.getKey())).setValue(out.get(i));
			++i;
		}
		return mg;
	}
	
	public abstract List<Double> correct(Collection<Double> pvalues, DoubleMatrix permutationPValues);
	
	public void init() {
		
	}
	
	public String toString() {
    	return PluginManager.getInstance().getPluginFromClass(getClass()).getName();
    }
    

}
