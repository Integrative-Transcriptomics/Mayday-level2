package mayday.gsanalysis;

import java.util.ArrayList;

public class Enrichment implements Comparable<Enrichment>{
	protected double pValue;
	protected double score;
	protected Geneset geneset;
	
	public Enrichment(Geneset geneset) {
		this.geneset=geneset;
	}
	
	public double getPValue() {
		return pValue;
	}
	
	public double getScore() {
		return score;
	}
	
	public Geneset getGeneset() {
		return geneset;
	}
	
	public void setScore(double score) {
		this.score=score;
	}
	
	public void setPValue(double pValue) {
		this.pValue=pValue;
	}

	@Override
	public int compareTo(Enrichment arg0) {
		if(getPValue()>arg0.getPValue()) {
			return -1;
		}
		else if(getPValue()==arg0.getPValue()) {
			return 0;
		}
		else {
			return 1;
		}
		
	}
	
	public boolean isSignificant() {
		if(pValue<=0.05) {
			return true;
		}
		else {
			return false;
		}
	}
	public String getNameOfValues() {
		return ("Geneset\tGenesetSize\tp-Value\tScore");
	}
	
	public ArrayList<String> getColumnIdentifiers() {
		ArrayList<String> identifiers = new ArrayList<String>();
		identifiers.add("p-Value");
		identifiers.add("Score");
		return identifiers;
	}
	
	public ArrayList<Double> getValues() {
		ArrayList<Double> values = new ArrayList<Double>();
		values.add(pValue);
		values.add(score);
		return values;
	}
	
	public String toString() {
		return (geneset.getName() + "\t" + "\t" + geneset.getGenes().size() + " " + "\t" + pValue + "\t" + score); 
	}
	
	public Enrichment clone() {
		Enrichment e = new Enrichment(geneset);
		e.pValue=pValue;
		e.score=score;
		return e;
	}

//	public String getQuickInfo() {
//		String info="P-value: " + pValue + "\nScore: "+ score;
//		return info;
//	}
	
}
