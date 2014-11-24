package mayday.Reveal.data;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.Reveal.data.attributes.SNPListAttribute;
import mayday.Reveal.events.SNPListEvent;
import mayday.Reveal.events.SNPListListener;
import mayday.Reveal.filter.RuleSet;
import mayday.core.EventFirer;

/**
 * @author jaeger
 *
 */
public class SNPList extends ArrayList<SNP> implements ChangeListener {
	
	private RuleSet ruleSet;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4279184318374691468L;
	private int start, stop;
	private int namelength = 0;
	
	//mapping identifiers to indices for faster SNP retrieval
	private Map<String, Integer> idMapping = new HashMap<String, Integer>();
	
	private boolean isSilent; // indicates whether listeners are notified or not
	private boolean topPriority = false;
	
	private SNPListAttribute attribute;
	private DataStorage dataStorage;
	
	private EventFirer<SNPListEvent, SNPListListener> eventfirer
    = new EventFirer<SNPListEvent, SNPListListener>() {
    	protected void dispatchEvent(SNPListEvent event, SNPListListener listener) {
    		listener.snpListChanged(event);
    	}		
    };
    
    public void addSNPListListener(SNPListListener listener) {
    	synchronized(this) {
    		eventfirer.addListener(listener);
    	}
    }
    
    public void setTopPriority(boolean topPriority) {
    	this.topPriority = topPriority;
    	fireSNPListChanged(SNPListEvent.ANNOTATION_CHANGE);
    }
    
    public boolean isTopPriority() {
    	return this.topPriority;
    }
    
    public void removeSNPListListener(SNPListListener listener) {
    	synchronized(this) {
    		eventfirer.removeListener(listener);
    	}
    }
    
    public List<SNPListListener> getSNPListListeners() {
    	return Collections.unmodifiableList(new ArrayList<SNPListListener>(eventfirer.getListeners()));
    }
    
    public void fireSNPListChanged(int change) {
        if (isSilent())
            return;
    	synchronized(this) {    		
    		eventfirer.fireEvent(new SNPListEvent(this, change));
    	}
    }
    
	/**
	 * @param title
	 */
	public SNPList(String name, DataStorage dataStorage) {
		super();
		this.attribute = new SNPListAttribute(this, name, null);
		this.dataStorage = dataStorage;
    	ruleSet = new RuleSet(this);
		ruleSet.addChangeListener(this);
	}
	
	public SNPListAttribute getAttribute() {
		return this.attribute;
	}

	public boolean add(SNP snp) {
		int pos = snp.getPosition();
		if(pos < start)
			start = pos;
		if(pos > stop)
			stop = pos;
		idMapping.put(snp.getID(), this.size());
		boolean added = super.add(snp);
		if(added)
			fireSNPListChanged(SNPListEvent.CONTENT_CHANGE);
		return added;
	}
	
	public boolean addAll(Collection<? extends SNP> snps) {
		boolean success = true;
		for(SNP s: snps) {
			if(!add(s))
				success = false;
		}
		if(!success) {
			removeAll(snps);
		} else {
			fireSNPListChanged(SNPListEvent.CONTENT_CHANGE);
		}
		return success;
	}
	
	public boolean removeAll(Collection<?> snps) {
		boolean success = true;
		for(Object o: snps) {
			if(o instanceof SNP) {
				SNP s = (SNP)o;
				if(!remove(s))
					success = false;
			}
		}
		if(success)
			fireSNPListChanged(SNPListEvent.CONTENT_CHANGE);
		return success;
	}
	
	public boolean remove(SNP snp) {
		boolean removed = super.remove(snp);
		if(removed) {
			idMapping.remove(snp.getID());
			fireSNPListChanged(SNPListEvent.CONTENT_CHANGE);
		}
		return removed;
	}
	
	/**
	 * @return snp range
	 */
	public int getSNPRange() {
		return stop - start + 1;
	}
	
	/**
	 * @return start position
	 */
	public int getStartPosition() {
		return this.start;
	}
	
	/**
	 * @return stop position
	 */
	public int getStopPosition() {
		return this.stop;
	}
	
	/**
	 * @return reference nucleotides
	 */
	public char[] getReferenceNucleotides() {
		char[] refs = new char[this.size()];
		for(int i = 0; i < this.size(); i++) {
			refs[i] = this.get(i).getReferenceNucleotide();
		}
		return refs;
	}
	
	/**
	 * @param identifier
	 * @return SNP with the specified identifier
	 */
	public SNP get(String identifier) {
		Integer index = idMapping.get(identifier);
		if(index != null) {
			return get(index.intValue());
		}
		return null;
	}
	
	/**
	 * @param chromosome
	 * @return SNPList of SNPs on the specified chromosome
	 */
	public SNPList getSNPsOnChromosome(String chromosome) {
		SNPList snps = new SNPList(this.attribute.getName(), this.dataStorage);
		for(int i = 0; i < this.size(); i++) {
			if(this.get(i).getChromosome().equals(chromosome)) {
				snps.add(this.get(i));
			}
		}
		return snps;
	}
	
	/**
	 * @return maximal name length
	 */
	public int getMaxNameLength() {
		if(namelength == 0) {
			for(SNP s: this) {
				if(s.getID().length() > namelength)
					namelength = s.getID().length();
			}
		}
		return namelength;
	}

	public void serialize(BufferedWriter bw) throws IOException {
		bw.append(">" + attribute.getName());
		bw.append("\t");
		bw.append(attribute.getInformation());
		bw.append("\n");
		
		for(SNP s: this) {
			bw.append(s.serialize());
			bw.append("\n");
		}
	}
	
	public DataStorage getDataStorage() {
		return this.dataStorage;
	}
	
	public void propagateClosing() {
		ruleSet.dispose();
		for(SNPListListener l : eventfirer.getListeners()) {
			eventfirer.removeListener(l);
		}
		idMapping.clear();
		clear();
	}
	
    public boolean isSilent() {
        return (isSilent);
    }
    
    public void setSilent(boolean isSilent) {
        this.isSilent = isSilent;
    }
    
    public void clear() {
    	super.clear();
    	this.idMapping.clear();
    	fireSNPListChanged(SNPListEvent.CONTENT_CHANGE);
    }
    
    public boolean equals(Object o) {
    	if(!(o instanceof SNPList)) {
    		return false;
    	}
    	return ((SNPList)o).getAttribute().getName().equals(this.getAttribute().getName());
    }
    
	public RuleSet getRuleSet() {
		return this.ruleSet;
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		try {
			populate();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	protected boolean isUpdating = false;
	
	protected void populate() {
		if(isUpdating) {
			System.err.println("Cyclic dependency in dynmic SNP list detected!");
			return;
		}
		
		if(this.getAttribute().getName().equals("Global")) {
			System.err.println("The global SNPList can not be modified!");
			return;
		}
		
		//do this step only if there already is a global snplist
		if(getDataStorage().getGlobalSNPList() != null) {
			isUpdating = true;
			boolean wasSilent = isSilent();
			setSilent(true);
			clear();
			// refilter, doing only changes that are really necessary
			for (SNP snp : getDataStorage().getGlobalSNPList()) {
				if (ruleSet.passesFilter(snp)==true) {
					if (!contains(snp))
						add(snp);	
				} else {
					if (contains(snp))
						remove(snp);
				}			
			}
			
			setSilent(wasSilent);
			fireSNPListChanged(SNPListEvent.CONTENT_CHANGE);
			isUpdating = false;
		}
	}
	
	public boolean contains(Object o) {
		if(!(o instanceof SNP))
			return false;
		SNP s = (SNP)o;
		
		if(idMapping.containsKey(s.getID())) {
			return true;
		}
		return false;
	}
	
	public boolean contains(String snpID) {
		return idMapping.containsKey(snpID);
	}
	
	public String toString() {
		return this.getAttribute().getName() + " (" + size() + ")";
	}


	public void setRuleSet(RuleSet ruleSet) {
		this.ruleSet = ruleSet;
	}
}
