package mayday.tiala.pairwise.suggestion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIType;
import mayday.core.meta.types.DoubleMIO;
import mayday.core.tasks.AbstractTask;
import mayday.tiala.pairwise.data.AlignedDataSets;
import mayday.tiala.pairwise.data.AlignmentStore;

public class ScoredAlignment implements Comparable<ScoredAlignment> {

	private double score;
	private double[] quantiles = new double[11];// 0,10,20,30,...,90,100 percent 
	private HashMap<String, Double> distances;
	private AlignedDataSets alignment;

	public ScoredAlignment(AlignedDataSets alignment, Boolean scoreAllProbes) {
		this.alignment = alignment;
		computeValues(scoreAllProbes);
	}
	
	public ScoredAlignment(AlignedDataSets alignment) {
		this(alignment, null);
	}

	protected static String formatDouble(double d) {
		return ""+(((double)Math.round((d*100)))/100);
	}
	
	public String toString() {
		String ret = alignment.getCommonCount()+" mapped experiments, shift "+alignment.getTimeShift()+", mean distance "+formatDouble(score);
		//+" [";
//		for (double q : quantiles)
//			ret+=formatDouble(q)+", ";
//		ret+="]";
		return ret;
	}

	public int compareTo(ScoredAlignment o) {
		int c = Double.valueOf(o.score).compareTo(score);
		if (c==0) 
			//compare Quantiles
			for (int i=0; i!=quantiles.length; ++i)
				if ((c = Double.valueOf(quantiles[i]).compareTo(o.quantiles[i])) !=0)
					break;
		if (c==0)
			c = Integer.valueOf(alignment.getCommonCount()).compareTo(o.alignment.getCommonCount());			
		return c;
	}
	
	protected void computeValues(Boolean fullSet) {		
		distances = alignment.getScores(fullSet);
		ArrayList<Double> cors = new ArrayList<Double>(distances.values());
		Collections.sort(cors, Collections.reverseOrder());
		int stepsize = cors.size()/10;
		if (cors.size()>0) {
			for (int i=0; i<=10; ++i)
				quantiles[i] = cors.get(Math.min(Math.max(0,stepsize*i),cors.size()-1));
			score = cors.get(cors.size()/2); // median-like
		} else {
			score = Double.NaN;
			for (int i=0; i<=10; ++i)
				quantiles[i] = Double.NaN;
		}
	}

	public AlignedDataSets getAlignment() {
		return alignment;
	}
	
	public Double getScore() {
		return score;
	}
	
	public double[] getQuantiles() {
		return quantiles;
	}

	public Map<String, Double> getDistances() {
		return Collections.unmodifiableMap(distances);
	}
	
	public static List<ScoredAlignment> generateList(final AlignmentStore ast) {
		final LinkedList<ScoredAlignment> ret = new LinkedList<ScoredAlignment>();
		AbstractTask at = new AbstractTask("Computing suggestions") {

			@Override
			protected void doWork() throws Exception {
				setProgress(-1);
				int i=0; 
				int size = ast.getPossibleAlignments().getAlignments().everything().size();
				for (Double shift : ast.getPossibleAlignments().getAlignments().everything()) {
					setProgress((i*10000)/size);
					AlignedDataSets ads = new AlignedDataSets(ast,ast.getOne(), ast.getTwo(), shift);
					ScoredAlignment sa = new ScoredAlignment(ads);
					ret.add(sa);
					++i;
				}
				Collections.sort(ret, Collections.reverseOrder());
				setProgress(10000);
			}

			protected void initialize() {
			}
		};
		at.start();
		at.waitFor();
		
		return ret;
	}
	
	public void addMIOs(DataSet ds) {
		MIGroup mg = ds.getMIManager().newGroup("PAS.MIO.Double", "Inter-DataSet Correlation");
		for (Entry<String,Double> e : distances.entrySet()) {
			Probe pb = ds.getMasterTable().getProbe(e.getKey());
			if (pb!=null) {
				MIType mt = mg.add(pb);
				((DoubleMIO)mt).setValue(e.getValue());
			}
		}
	}
	
}