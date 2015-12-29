package mayday.motifsearch.model;

import java.util.*;

import mayday.core.Probe;
import mayday.motifsearch.model.Sites;


public class Sequence {
    private String ID;
    private String name; // has to be the same as the synonym name of the genes
    private int length;
    private Sites sites;
    private boolean isPlusStrand = true;
    private int TlSSPosition = -1; // default value is -1 and is not drawn for that position
    private Long exctractedFromPosition = Long.MIN_VALUE; 
    private Long exctractedToPosition = Long.MIN_VALUE;
    
    private List<Probe> probes;

    public Sequence(String id, String name, int length) {
	super();
	ID = id;
	this.name = name;
	this.length = length;
	this.sites = new Sites();
	this.probes = new ArrayList<Probe>();
    }
    
    public String[] getProbeNames() {
    	if(this.probes != null) {
    		String[] res = new String[probes.size()];
    		for(int i = 0; i < probes.size(); i++) {
    			res[i] = probes.get(i).getName();
    		}
    		return res;
    	}
    		
    	return null;
    }

    public void addSite(Site site){
	this.sites.add(site);
    }


    public String getID() {
	return ID;
    }



    public int getTlSSPosition() {
	return TlSSPosition;
    }


    public void setTlSSPosition(int tlSSPosition) {
	TlSSPosition = tlSSPosition;
    }

    public Sites getSites() {
	return sites;
    }


    public String getName() {
	return name;
    }

    public int getLength() {
	return length;
    }

    public boolean isPlusStrand() {
	return isPlusStrand;
    }


    public void setPlusStrand(boolean isPlusStrand) {
	this.isPlusStrand = isPlusStrand;
    } 

    /**
     * checks if given motifs are on the Sequence with or modus.
     * 
     * @param motifs
     *                the given motifs
     * @return boolean do some motifs bind?
     */
    public final boolean updateSiteListOR(ArrayList<Motif> motifs,
	    Sequence sequence) {
	Sites tempSites = this.getSites().getSitesFromMotifs(motifs);
	sequence.setSites(tempSites);

	return tempSites.isEmpty();
    }

    /**
     * checks if given motifs are on the Sequence with and modus.
     * 
     * @param motifs
     *                the given motifs
     * @return boolean do all motifs bind?
     */

    public final boolean updateSiteListAND(ArrayList<Motif> motifs,
	    Sequence sequence) {
	for(Motif m: motifs){
	    ArrayList<Motif> dummyMotifs = new ArrayList<Motif>();
	    dummyMotifs.add(m);
	    Sites tempSites = this.getSites().getSitesFromMotifs(dummyMotifs);
	    for(Site s: tempSites){
		sequence.addSite(s);
	    }
	    if (tempSites.isEmpty()){
		sequence.getSites().clear();
		return false;
	    }

	}
	return true;


    }

    public final Double getMinSignificanceValue(){
	return this.sites.getMinSignificanceValue();
    }

    public final Double getMaxSignificanceValue(){
	return this.sites.getMaxSignificanceValue();
    }

    public Sequence cloneBasics() {
	Sequence s = new Sequence(this.ID, this.name, this.length);
	s.setPlusStrand(this.isPlusStrand);
	s.setTlSSPosition(this.TlSSPosition);
	s.setExctractedFromPosition(this.exctractedFromPosition);
	s.setExctractedToPosition(this.exctractedToPosition); 
	s.setProbes(this.getProbes());
	return s;
    }

    private void setProbes(List<Probe> probes) {
		this.probes.addAll(probes);
	}

	private List<Probe> getProbes() {
		return this.probes;
	}

	public final void setSites(Sites sites) {
	this.sites = sites;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((ID == null) ? 0 : ID.hashCode());
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
	final Sequence other = (Sequence) obj;
	if (ID == null) {
	    if (other.ID != null)
		return false;
	}
	else if (!ID.equals(other.ID))
	    return false;
	return true;
    }

    @Override
    public String toString(){

	return "name: " + this.getName() + " "
	+ "# sites: " + this.getSites().size()+ " "
	+ "length: " + this.getLength()+ " "  
	+ "strand: " + (this.isPlusStrand()?"+":"-")
	+
	((this.exctractedFromPosition != Long.MIN_VALUE) && (this.exctractedToPosition!= Long.MIN_VALUE)
		?" extracted from positions in chromosome: "
			+ this.exctractedFromPosition +"-"+ this.exctractedToPosition:"") ;
    }


    public void setExctractedFromPosition(long exctractedFromPosition) {
	this.exctractedFromPosition = exctractedFromPosition;
    }


    public void setExctractedToPosition(long exctractedToPosition) {
	this.exctractedToPosition = exctractedToPosition;
    }

    public double getMeanSignificanceOfSites(){
	double val1 = (this.getSites().isEmpty()?Double.MAX_VALUE:0);
	for (Site s : this.getSites()) {
	    val1 += s.getSignificanceValue();
	}
	return val1/this.getSites().size();
    }

    public void addProbe(Probe p) {
    	this.probes.add(p);
    }
}
