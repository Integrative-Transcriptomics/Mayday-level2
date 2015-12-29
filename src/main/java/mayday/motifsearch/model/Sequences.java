package mayday.motifsearch.model;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A list of sequences. Also holds the sequence with the longest length seen so far while added.
 * 
 * Important: removing the longest sequence has no effect on storage of it internally! 
 * 
 * @author Frederik Weber
 * 
 */
public class Sequences
extends ArrayList<Sequence> {

    private static final long serialVersionUID = 1L;

    protected Sequence longestSequence = null;
    protected Double minSignificanceValue;
    protected Double maxSignificanceValue; 

    public Sequences() {
	super();
    }

    /**
     * Adds an sequence to the List of Sequences
     * 
     */
    @Override
    public boolean add(Sequence arg0) {
	if (this.longestSequence == null) {
	    this.longestSequence = arg0;
	}
	else {
	    if (arg0.getLength() > this.longestSequence
		    .getLength()) {
		this.longestSequence = arg0;
	    }
	}

	if (arg0.getMinSignificanceValue() != null){
	    if (this.minSignificanceValue == null) {
		this.minSignificanceValue = arg0.getMinSignificanceValue();
	    }
	    else {
		double tempDouble = arg0.getMinSignificanceValue(); 
		if (tempDouble < this.minSignificanceValue) {
		    this.minSignificanceValue = tempDouble;
		}
	    }
	}

	if (arg0.getMaxSignificanceValue() != null) {
	    if (this.maxSignificanceValue == null) {
		this.maxSignificanceValue = arg0.getMinSignificanceValue();
	    }
	    else {
		double tempDouble = arg0.getMaxSignificanceValue(); 
		if (tempDouble > this.maxSignificanceValue) {
		    this.maxSignificanceValue = tempDouble;
		}
	    }
	}
	SiteComparator c = new SiteComparator(
		SiteComparator.SORT_BY_SIGNIFICANCE_VALUE);
	Collections.sort(arg0.getSites(), c);
	return super.add(arg0);
    }

    /**
     * Returns the length of the longest sequence
     * 
     * @return length of Sequence
     */

    public int getLongestSequenceLength() {
	if (this.longestSequence != null) {
	    return this.longestSequence.getLength();
	}
	return 0;
    }

    public int getNumberOfMotifOccurrence(Motif motif) {
	int tempNumber = 0;
	for (Sequence sequence : this) {
	    for (Site site : sequence.getSites()) {
		if (site.getMotif().equals(motif)){
		    tempNumber++;
		}
	    }	    
	}
	return tempNumber;
    }


    public Double getMinSignificanceValue() {
	Double tempDouble = null;
	for (Sequence sequence : this) {
	    Double sigVal = sequence.getMinSignificanceValue();
	    if (sigVal != null){
		if (tempDouble == null){
		    tempDouble = sigVal;
		} else {
		    if (tempDouble > sigVal){
			tempDouble = sigVal;
		    }
		}
	    }
	}
	return tempDouble;
    }


    public Double getMaxSignificanceValue() {
	Double tempDouble = null;

	for (Sequence sequence : this) {
	    Double sigVal = sequence.getMaxSignificanceValue();
	    if (sigVal != null){
		if (tempDouble == null){
		    tempDouble = sigVal;
		} else {
		    if (tempDouble < sigVal){
			tempDouble = sigVal;
		    }
		}
	    }
	}
	return tempDouble;
    }


}
