package mayday.Reveal.visualizations.snpmap.aggregation;

import java.util.HashSet;
import java.util.Set;

import mayday.Reveal.data.Subject;

public class Aggregation {
	
	private double[] aggregationValues;
	private double[] frequencies;
	
	private String name;
	private Set<Subject> aggregatedSubjects;
	
	public Aggregation(int size) {
		this.aggregationValues = new double[size];
		this.frequencies = new double[size];
	}
	
	public double getAggregationValue(int index) {
		return this.aggregationValues[index];
	}
	
	public double getFrequency(int index) {
		return this.frequencies[index];
	}
	
	public void setAggregationValue(int index, double value) {
		this.aggregationValues[index] = value;
	}
	
	public void setFrequency(int index, double frequency) {
		this.frequencies[index] = frequency;
	}

	public int size() {
		return aggregationValues.length;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void setSubjects(Set<Subject> subjects) {
		this.aggregatedSubjects = new HashSet<Subject>(subjects);
	}
	
	public String getName() {
		return this.name;
	}
	
	public Set<Subject> getAggregatedSubjects() {
		return this.aggregatedSubjects;
	}
}
