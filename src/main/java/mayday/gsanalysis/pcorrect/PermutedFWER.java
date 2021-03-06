package mayday.gsanalysis.pcorrect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import mayday.core.math.Statistics;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.linalg.matrix.DoubleMatrix;

public class PermutedFWER extends PValueCorrectionWithPermutationsPlugin{

	
	private int smallerOrEqualValues(List<Double> values, double value) {
		int pos=Collections.binarySearch(values,value);
		if(pos<0) {
			return (pos+1)*(-1);
		}
		else {
			for(int i=pos+1;i!=values.size();i++) { 
				if(values.get(i)>value) {
					return i;
				}
			}
		}
		return values.size();
	}
	@Override
	public List<Double> correct(Collection<Double> pvalues,
			DoubleMatrix permutationPValues) {
		
		int size = pvalues.size();
		ArrayList<Double> ret = new ArrayList<Double>();

		List<Double> lpvalues;
		if (!(pvalues instanceof List))
			lpvalues = new ArrayList<Double>(pvalues);
		else
			lpvalues = (List<Double>)pvalues;
	
		List<Double> lpvaluesc=new ArrayList<Double>();
		DoubleMatrix permutationPValuesc=new DoubleMatrix(permutationPValues.nrow(),permutationPValues.ncol());
		//first determine p-values only within one column
		for(int l=0;l!=permutationPValues.ncol();l++) {
			List<Double> pValues = permutationPValues.getColumn(l).clone().asList();
			Collections.sort(pValues);
			int smaller=smallerOrEqualValues(pValues,lpvalues.get(l));
			lpvaluesc.add((double)smaller/permutationPValues.nrow());
			
			
			for(int k=0;k!=permutationPValues.nrow();k++) {
				smaller=smallerOrEqualValues(pValues,permutationPValues.getValue(k,l));
				permutationPValuesc.setValue(k,l,(double)smaller/permutationPValues.nrow());
				
			}
			
			
		}
		
		lpvalues=lpvaluesc;
		permutationPValues=permutationPValuesc;
		//rank p-values, the highest value gets the 1st rank
		
		List<Integer> ranks = Statistics.rank(lpvalues);
		
		HashMap<Integer,Integer> rankMap = new HashMap<Integer,Integer>();
		int position = 0;
		for(int rank:ranks) {
			rankMap.put(rank, position);
			position++;
		}
		
		for (double p : lpvalues) {
			double counter=0;
			for(int k=0;k!=permutationPValues.nrow();k++) {
				double min = permutationPValues.getRow(k).min();
				if(min<=p) {
					counter++;
				}
			}
			
			double pc=((double)counter)/permutationPValues.nrow();
			
			ret.add(pc);
			
		}
		
		
		for(int rank=2;rank<=size;rank++) {
			int currentIndex = rankMap.get(rank);
			int nextIndex = rankMap.get(rank-1);
			double pValue=Math.max(ret.get(currentIndex),ret.get(nextIndex));
			ret.set(currentIndex,pValue);
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.pcorrection.FWER (permutations)",
				new String[0],
				MC,
				new HashMap<String, Object>(),
				"Roland Keller",
				"kellerr@informatik.uni-tuebingen.de",
				"FWER (Westfall, Young) p-value correction",
				"FWER (permutations)"
				);
		

		return pli;
	}

}

