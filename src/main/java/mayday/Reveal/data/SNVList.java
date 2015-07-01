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

import mayday.Reveal.data.attributes.SNVListAttribute;
import mayday.Reveal.events.SNVListEvent;
import mayday.Reveal.events.SNVListListener;
import mayday.Reveal.filter.RuleSet;
import mayday.core.EventFirer;

/**
 * @author jaeger
 *
 */
public class SNVList extends ArrayList<SNV> implements ChangeListener {
	
	private RuleSet ruleSet;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4279184318374691468L;
	private int start, stop;
	private int namelength = 0;
	
	//mapping identifiers to indices for faster SNP retrieval
	private Map<String, Integer> idMapping = new HashMap<String, Integer>();
	
	private List<Integer> indexList;
	
	private boolean isSilent; // indicates whether listeners are notified or not
	private boolean topPriority = false;
	
	private SNVListAttribute attribute;
	private DataStorage dataStorage;
	
	private EventFirer<SNVListEvent, SNVListListener> eventfirer
    = new EventFirer<SNVListEvent, SNVListListener>() {
    	protected void dispatchEvent(SNVListEvent event, SNVListListener listener) {
    		listener.snpListChanged(event);
    	}		
    };
    
    public void addSNVListListener(SNVListListener listener) {
    	synchronized(this) {
    		eventfirer.addListener(listener);
    	}
    }
    
    public void setTopPriority(boolean topPriority) {
    	this.topPriority = topPriority;
    	fireSNVListChanged(SNVListEvent.ANNOTATION_CHANGE);
    }
    
    public boolean isTopPriority() {
    	return this.topPriority;
    }
    
    public void removeSNVListListener(SNVListListener listener) {
    	synchronized(this) {
    		eventfirer.removeListener(listener);
    	}
    }
    
    public List<SNVListListener> getSNVListListeners() {
    	return Collections.unmodifiableList(new ArrayList<SNVListListener>(eventfirer.getListeners()));
    }
    
    public void fireSNVListChanged(int change) {
        if (isSilent())
            return;
    	synchronized(this) {    		
    		eventfirer.fireEvent(new SNVListEvent(this, change));
    	}
    }
    
	/**
	 * @param title
	 */
	public SNVList(String name, DataStorage dataStorage) {
		super();
		this.attribute = new SNVListAttribute(this, name, null);
		this.dataStorage = dataStorage;
    	ruleSet = new RuleSet(this);
		ruleSet.addChangeListener(this);
	}
	
	public SNVListAttribute getAttribute() {
		return this.attribute;
	}

	public boolean add(SNV snv) {
		int pos = snv.getPosition();
		if(pos < start)
			start = pos;
		if(pos > stop)
			stop = pos;
		idMapping.put(snv.getID(), this.size());
		boolean added = super.add(snv);
		if(added)
			fireSNVListChanged(SNVListEvent.CONTENT_CHANGE);
		return added;
	}
	
	public boolean addAll(Collection<? extends SNV> snvs) {
		boolean success = true;
		for(SNV s: snvs) {
			if(!add(s))
				success = false;
		}
		if(!success) {
			removeAll(snvs);
		} else {
			fireSNVListChanged(SNVListEvent.CONTENT_CHANGE);
		}
		return success;
	}
	
	public boolean removeAll(Collection<?> snps) {
		boolean success = true;
		for(Object o: snps) {
			if(o instanceof SNV) {
				SNV s = (SNV)o;
				if(!remove(s))
					success = false;
			}
		}
		if(success)
			fireSNVListChanged(SNVListEvent.CONTENT_CHANGE);
		return success;
	}
	
	public boolean remove(SNV snp) {
		boolean removed = super.remove(snp);
		if(removed) {
			idMapping.remove(snp.getID());
			fireSNVListChanged(SNVListEvent.CONTENT_CHANGE);
		}
		return removed;
	}
	
	/**
	 * @return snp range
	 */
	public int getSNVRange() {
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
	public SNV get(String identifier) {
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
	public SNVList getSNVsOnChromosome(String chromosome) {
		SNVList snps = new SNVList(this.attribute.getName(), this.dataStorage);
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
			for(SNV s: this) {
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
		
		for(SNV s: this) {
			bw.append(s.serialize());
			bw.append("\n");
		}
	}
	
	public DataStorage getDataStorage() {
		return this.dataStorage;
	}
	
	public void propagateClosing() {
		ruleSet.dispose();
		for(SNVListListener l : eventfirer.getListeners()) {
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
    	fireSNVListChanged(SNVListEvent.CONTENT_CHANGE);
    }
    
    public boolean equals(Object o) {
    	if(!(o instanceof SNVList)) {
    		return false;
    	}
    	return ((SNVList)o).getAttribute().getName().equals(this.getAttribute().getName());
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
		if(getDataStorage().getGlobalSNVList() != null) {
			isUpdating = true;
			boolean wasSilent = isSilent();
			setSilent(true);
			clear();
			// refilter, doing only changes that are really necessary
			for (SNV snp : getDataStorage().getGlobalSNVList()) {
				if (ruleSet.passesFilter(snp)==true) {
					if (!contains(snp))
						add(snp);	
				} else {
					if (contains(snp))
						remove(snp);
				}			
			}
			
			setSilent(wasSilent);
			fireSNVListChanged(SNVListEvent.CONTENT_CHANGE);
			isUpdating = false;
		}
	}
	
	public boolean contains(Object o) {
		if(!(o instanceof SNV))
			return false;
		SNV s = (SNV)o;
		
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

	public List<Integer> getIndexList() {
		if(this.indexList == null) {
			indexList = new ArrayList<Integer>();
			for(int i = 0; i < size(); i++) {
				indexList.add(get(i).getIndex());
			}
		}
		return indexList;
	}
}
