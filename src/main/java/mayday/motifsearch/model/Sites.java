package mayday.motifsearch.model;

import java.util.ArrayList;

/**
 * A list of Sites.
 * 
 * @author Frederik Weber
 * 
 */
public class Sites
extends ArrayList<Site> {

    private static final long serialVersionUID = 1L;


    /**
     * adds a site
     * 
     */
    @Override
    public boolean add(Site site) {
	return super.add(site);
    }

    /**
     *returns the Number of Sites that match to
     * the given Motif.
     * 
     * @param motif
     *                the selected motif
     * @return Number of Sites
     */

    public int countSites(Motif motif) {

	int numberOfSites = 0;

	for (Site s : this) {
	    if (s.getMotif().equals(motif)) {
		numberOfSites += 1;
	    }
	}

	return numberOfSites;
    }

    public Sites getSitesFromMotifs(ArrayList<Motif> motifs) {
	Sites tempSites = new Sites();
	for (Motif motif: motifs){
	    for (Site s : this) {
		if (s.getMotif().equals(motif)) {
		    tempSites.add(s);
		}
	    }
	}
	return tempSites;
    }

    public int getNumberOfDifferentMotifs() {
	ArrayList<Motif> tempMotifs = new ArrayList<Motif>();
	for (Site s : this) {
	    Motif m = s.getMotif();
	    if (!tempMotifs.contains(m)) {
		tempMotifs.add(m);
	    }
	}
	return tempMotifs.size();
    }

    public final Double getMinSignificanceValue(){
	Double tempDouble = null;
	for (Site site : this) {
	    if (tempDouble == null){
		tempDouble = site.getSignificanceValue();
	    } else {
		if (tempDouble > site.getSignificanceValue()){
		    tempDouble = site.getSignificanceValue();
		}
	    }
	}
	return tempDouble;
    }

    public final Double getMaxSignificanceValue(){
	Double tempDouble = null;
	for (Site site : this) {
	    if (tempDouble == null){
		tempDouble = site.getSignificanceValue();
	    } else {
		if (tempDouble < site.getSignificanceValue()){
		    tempDouble = site.getSignificanceValue();
		}
	    }
	}
	return tempDouble;
    }


}

