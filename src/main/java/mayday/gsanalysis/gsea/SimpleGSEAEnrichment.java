package mayday.gsanalysis.gsea;

import java.util.ArrayList;

import mayday.gsanalysis.Enrichment;
import mayday.gsanalysis.Geneset;


public class SimpleGSEAEnrichment extends Enrichment{

	private double scaleScore;
	private double scalePValue;
	private double zPValue;
	private int zRank;
	private int scaleRank;;
	
	public SimpleGSEAEnrichment(Geneset geneset) {
		super(geneset);
	}
	
	public void setZPValue(double zPValue) {
		this.zPValue = zPValue;
	}
	public void setScaleScore(double scaleScore) {
		this.scaleScore = scaleScore;
	}
	public void setScalePValue(double scalePValue) {
		this.scalePValue = scalePValue;
	}
	
	public double getZPValue() {
		return zPValue;
	}
	
	public double getZRank() {
		return zRank;
	}
	
	public double getScaleRank() {
		return scaleRank;
	}
	
	public double getScaleScore() {
		return scaleScore;
	}
	
	public double getScalePValue() {
		return scalePValue;
	}
	
	public String getNameOfValues() {
		return ("Geneset\tGenesetSize\tp-Value\tz-Score\tp-Value (z)\tscale-Score\tp-value (scale)");
	}
	public String toString() {
		return (geneset.getName() + "\t" + geneset.getGenes().size() + "\t" + pValue + "\t" + score+ "\t" + zPValue + "\t" + scaleScore + "\t" + scalePValue);
	}
	
	public ArrayList<Double> getValues() {
		ArrayList<Double> values = super.getValues();
		values.add((double)zRank);
		values.add(zPValue);
		values.add(scaleScore);
		values.add((double)scaleRank);
		values.add(scalePValue);
		
		return values;
	}
	
	public ArrayList<String> getColumnIdentifiers() {
		ArrayList<String> identifiers = new ArrayList<String>();
		identifiers.add("p-Value");
		identifiers.add("z-Score");
		identifiers.add("Rank (z)");
		identifiers.add("p-Value (z)");
		identifiers.add("Scale-Score");
		identifiers.add("Rank (scale)");
		identifiers.add("p-Value (scale)");
		
		return identifiers;
	}

	public void setZRank(int zRank) {
		this.zRank=zRank;
	}

	public void setScaleRank(int scaleRank) {
		this.scaleRank=scaleRank;
	}
	
//	@Override
//	public String getQuickInfo() {
//		String info="P-value: " + pValue + "\nZ-score: "+ score + "\n P-value (Z-score): " + zPValue + "\nScale score: "+ scaleScore + "\nScale P-value: " + scalePValue;
//		return info;
//	}

}
