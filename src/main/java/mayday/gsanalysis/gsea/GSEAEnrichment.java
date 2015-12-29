package mayday.gsanalysis.gsea;

import java.util.ArrayList;

import mayday.core.structures.linalg.matrix.PermutableMatrix;
import mayday.core.structures.linalg.vector.DoubleVector;
import mayday.gsanalysis.Enrichment;
import mayday.gsanalysis.Geneset;

public class GSEAEnrichment extends Enrichment{
	private PermutableMatrix leadingEdge;
	private double unnormalizedScore;
	private double FWERhist;
	private double FDRhist;
	private DoubleVector permutationScores;
	private DoubleVector normalizedPermutationScores;
	
	public GSEAEnrichment(Geneset geneset,int nPermutations) {
		super(geneset);
		permutationScores=new DoubleVector(nPermutations);
		normalizedPermutationScores=new DoubleVector(nPermutations);
	}
	
	public void setUnnormalizedScore(double unnormalizedScore) {
		this.unnormalizedScore = unnormalizedScore;
	}
	
	public void setFWERhist(double FWERhist) {
		this.FWERhist = FWERhist;
	}
	
	public void setFDRhist(double FDRhist) {
		this.FDRhist=FDRhist;
	}
	
	public void setLeadingEdge(PermutableMatrix permutableMatrix) {
		this.leadingEdge=permutableMatrix;
	}
	
	@Override
	public boolean isSignificant() {
		if(FDRhist<=0.25) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public void setPermutationScore(int position,double permutationScore) {
		permutationScores.set(position,permutationScore);
	}
	
	public void setNormalizedPermutationScore(int position,double permutationScore) {
		normalizedPermutationScores.set(position,permutationScore);
	}
	
	public double getUnnormalizedScore() {
		return unnormalizedScore;
	}
	
	public double getFWERhist() {
		return FWERhist;
	}
	
	public double getFDRhist() {
		return FDRhist;
	}
	
	public PermutableMatrix getLeadingEdge() {
		return leadingEdge;
	}
	
	public DoubleVector getPermutationScores() {
		return permutationScores;
	}
	
	public DoubleVector getNormalizedPermutationScores() {
		return normalizedPermutationScores;
	}
	
	public String getNameOfValues() {
		return ("Geneset\tGenesetSize\tNominalP-Value\tNES\tES\tFWER-p\tFDR-q");
	}
	public String toString() {
		return (geneset.getName() + "\t" + geneset.getGenes().size() + "\t" + pValue + "\t" + score+ "\t" + unnormalizedScore + "\t" + FWERhist +"\t" + FDRhist);
	}
	
	public ArrayList<Double> getValues() {
		ArrayList<Double> values = super.getValues();
		values.add(unnormalizedScore);
		values.add(FWERhist);
		values.add(FDRhist);
		return values;
	}
	
	public ArrayList<String> getColumnIdentifiers() {
		ArrayList<String> identifiers = new ArrayList<String>();
		identifiers.add("Nominal p-Value");
		identifiers.add("NES");
		identifiers.add("ES");
		identifiers.add("FWER-p");
		identifiers.add("FDR-q");
		return identifiers;
	}
	
	@Override
	public int compareTo(Enrichment arg0) {
		if(arg0 instanceof GSEAEnrichment) {
			if(getFDRhist()>((GSEAEnrichment)arg0).getFDRhist()) {
				return -1;
			}
			else if(getFDRhist()==((GSEAEnrichment)arg0).getFDRhist()) {
				return 0;
			}
			else {
				return 1;
			}
		}
		else {
			return super.compareTo(arg0);
		}
		
	}
	
//	@Override
//	public String getQuickInfo() {
//		String info="NES: "+ score + "\nNominal P-value: " + pValue + "\nFDR: "+ FDRhist + "\nFWER: "+ FWERhist;
//		return info;
//	}

}
