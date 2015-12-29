package mayday.motifsearch.preparation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.io.*;


public class Chromosome {

    private Header header;
    private File SequenceFASTAFile; 

    private ConcurrentHashMap<String, GeneLocation> geneLocationHashMap;
    public ArrayList<GeneLocation> geneLocationArrayList = new ArrayList<GeneLocation>();
    private ArrayList<GeneLocation> tempNeededgeneLocationArrayList = new ArrayList<GeneLocation>();

    public Chromosome(Header header, File SequenceFASTAFile) {
	super();
	this.header = header;
	this.SequenceFASTAFile = SequenceFASTAFile;
    }

    public Header getHeader() {
	return this.header;
    }

    public File getSequenceFASTAFile() {
	return this.SequenceFASTAFile;
    }


    public ConcurrentHashMap<String, GeneLocation> getGeneLocationHashMap() {
	return this.geneLocationHashMap;
    }


    public void setGeneLocationHashMap(
	    ConcurrentHashMap<String, GeneLocation> geneLocationHashMap) {
	this.geneLocationHashMap = geneLocationHashMap;
	this.geneLocationArrayList.addAll(geneLocationHashMap.values());
	GeneticCoordinateComparator geneticCoordianteComparator = new GeneticCoordinateComparator(GeneticCoordinateComparator.FIRST_OCCURRENCE_FIRST);
	Collections.sort(this.geneLocationArrayList, geneticCoordianteComparator);
	for (GeneLocation geneLoc: this.geneLocationArrayList){
	    geneLoc.setChromosome(this);
	}
    }


    public void sortTempNeededGeneLocations(byte method) {
	GeneticCoordinateComparator geneticCoordianteComparator = new GeneticCoordinateComparator(method);
	Collections.sort(this.tempNeededgeneLocationArrayList, geneticCoordianteComparator);
    }

    public void addUnsortedTempNeededgeneLocation(GeneLocation geneLocation) {
	this.tempNeededgeneLocationArrayList.add(geneLocation);
    }


    public ArrayList<GeneLocation> getTempNeededgeneLocationArrayList() {
	return this.tempNeededgeneLocationArrayList;
    }

    public GeneLocation getGeneLocationSuccessor(GeneLocation geneLocation){
	int tmpIndx = this.geneLocationArrayList.indexOf(geneLocation);
	GeneLocation predecessorGeneLocation = null;
	if((tmpIndx > 0)){
	    predecessorGeneLocation = this.geneLocationArrayList.get(tmpIndx-1);
	}
	return predecessorGeneLocation;
    }

    @Override
    public String toString()
    {
	return this.header.getSequenceName();
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime
	* result
	+ ((SequenceFASTAFile == null) ? 0 : SequenceFASTAFile
		.hashCode());
	result = prime * result + ((header == null) ? 0 : header.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	Chromosome other = (Chromosome) obj;
	if (SequenceFASTAFile == null) {
	    if (other.SequenceFASTAFile != null)
		return false;
	}
	else if (!SequenceFASTAFile.equals(other.SequenceFASTAFile))
	    return false;
	if (header == null) {
	    if (other.header != null)
		return false;
	}
	else if (!header.equals(other.header))
	    return false;
	return true;
    }

}
