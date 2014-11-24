package mayday.Reveal.data;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

import mayday.Reveal.data.attributes.DataStorageAttribute;
import mayday.Reveal.data.ld.old.LDStructure;
import mayday.Reveal.data.meta.Genome;
import mayday.Reveal.data.meta.MetaInformation;
import mayday.Reveal.data.meta.MetaInformationManager;
import mayday.Reveal.listeners.DataStorageEvent;
import mayday.Reveal.listeners.DataStorageListener;
import mayday.Reveal.utilities.DateTimeProvider;
import mayday.Reveal.viewmodel.SNPSorter;
import mayday.core.DataSet;
import mayday.core.EventFirer;

/**
 * @author jaeger
 *
 */
public class DataStorage {
	
	protected DataStorageAttribute attribute;

	protected GeneList genes;
	protected SubjectList persons;
	protected List<SNPList> snpLists;
	protected HaplotypesList haplotypes;
	
	protected MetaInformationManager metaInformationManager;
	
	private DataSet maydayDataSet;
	
	private ArrayList<LDStructure> ldStructures = new ArrayList<LDStructure>();
	
	/**
	 * Create a new DataStorage object
	 */
	public DataStorage(ProjectHandler projectHandler) {
		this.projectHandler = projectHandler;
		this.snpLists = new LinkedList<SNPList>();
		this.attribute = new DataStorageAttribute(this);
		this.metaInformationManager = new MetaInformationManager(this);
	}
	
	public DataStorageAttribute getAttribute() {
		return this.attribute;
	}
	
    private EventFirer<DataStorageEvent, DataStorageListener> eventfirer
    = new EventFirer<DataStorageEvent, DataStorageListener>() {
    	protected void dispatchEvent(DataStorageEvent event, DataStorageListener listener) {
    		listener.dataChanged(event);
    	}		
    };

	private ProjectHandler projectHandler;
    
    /**
     * @return number of external snp lists
     */
    public int numberOfSNPLists() {
    	return this.snpLists.size();
    }
    
    /**
     * @param name
     * @param external
     */
    public void addSNPList(String name, SNPList external) {
    	if(external == null)
			return;
    	if(name.equals("Global")) {
    		if(getGlobalSNPList() == null)
    			this.setGlobalSNPList(external);
    		else
    			return;
    	} else {
    		SNPList contained = getSNPList(name);
        	if(contained != null) {
    			name = DateTimeProvider.extendByTime(name);
    		}
        	
        	SNPList snps = getGlobalSNPList();
        	SNPList filtered = new SNPList(name, this);
        	
    		for(SNP s : external) {
    			if(snps.contains(s)) {
    				filtered.add(s);
    			}
    		}
    		
    		filtered.getAttribute().setInformation(external.getAttribute().getInformation());
    		filtered.setRuleSet(external.getRuleSet());
    		
    		if(filtered.size() > 0) {
    			this.snpLists.add(filtered);
    		}
    	}
		
		fireDataStorageChanged(DataStorageEvent.DATA_CHANGED);
    }
    
	public void removeSNPList(SNPList snpList) {
		if(snpList.getAttribute().getName().equals("Global")) {
			JOptionPane.showMessageDialog(null, "The global SNPList cannot be removed!");
			return;
		}
			
		if(snpLists.contains(snpList)) {
			snpLists.remove(snpList);
			fireDataStorageChanged(DataStorageEvent.DATA_CHANGED);
		}
	}
	
	public void removeSNPLists(Collection<SNPList> snpLists) {
		boolean globalDetected = false;
		boolean changed = false;
		
		for(SNPList s : snpLists) {
			if(s.getAttribute().getName().equals("Global")) {
				globalDetected = true;
				continue;
			}
			if(this.snpLists.contains(s)) {
				this.snpLists.remove(s);
				changed = true;
			}
		}
		
		if(globalDetected) {
			JOptionPane.showMessageDialog(null, "The global SNPList cannot be removed!");
		}
		
		if(changed) {
			fireDataStorageChanged(DataStorageEvent.DATA_CHANGED);
		}
	}
	
	public void removeSNPList(String name) {
		SNPList toRemove = getSNPList(name);
		if(toRemove != null) {
			removeSNPList(toRemove);
		}
	}
    
    /**
     * @param name
     * @return snplist with the specified name
     */
    public SNPList getSNPList(String name) {
    	for(SNPList snpList : snpLists) {
    		if(snpList.getAttribute().getName().equals(name)) {
    			return snpList;
    		}
    	}
    	return null;
    }
    
    /**
     * @return names of all external snp lists
     */
    public Set<String> getSNPListNames() {
    	Set<String> names = new HashSet<String>();
    	for(SNPList snpList : snpLists) {
    		names.add(snpList.getAttribute().getName());
    	}
    	return names;
    }
	
	/**
	 * @param genome
	 */
	public void setGenome(Genome genome) {
		List<MetaInformation> mios = this.metaInformationManager.get(Genome.MYTYPE);
		
		if(mios == null || mios.size() == 0) {
			//just add the genome
		} else {
			mios.clear();
		}
		
		this.metaInformationManager.add(Genome.MYTYPE, genome);
		this.fireDataStorageChanged(DataStorageEvent.META_INFORMATION_CHANGED);
	}
	
	/**
	 * @return the genome
	 */
	public Genome getGenome() {
		List<MetaInformation> mios = metaInformationManager.get(Genome.MYTYPE);
		if(mios == null || mios.size() == 0) {
//			JOptionPane.showMessageDialog(null, "No genome has been loaded so far.");
			return null;
		}
		
		Genome genome = (Genome) mios.get(0);
		return genome;
	}
	
	/**
	 * @param genes
	 */
	public void setGenes(GeneList genes) {
		this.genes = genes;
		this.fireDataStorageChanged(DataStorageEvent.DATA_CHANGED);
	}
	
	/**
	 * @param snps
	 */
	public void setGlobalSNPList(SNPList snps) {
		if(getGlobalSNPList() == null) {
			SNPSorter sorter = new SNPSorter(this);
			SNPList sorted = sorter.getSortedSNPList(snps, SNPSorter.GENOMIC_LOCATION);
			sorted.getAttribute().setName("Global");
			sorted.getAttribute().setInformation("Unmodifyable");
			snpLists.add(0, sorted);
			
			this.fireDataStorageChanged(DataStorageEvent.DATA_CHANGED);
		}
	}
	
	/**
	 * @param haplotypes
	 */
	public void setHaplotypes(HaplotypesList haplotypes) {
		this.haplotypes = haplotypes;
		this.fireDataStorageChanged(DataStorageEvent.DATA_CHANGED);
	}
	
	/**
	 * @param persons
	 */
	public void setSubjects(SubjectList persons) {
		this.persons = persons;
		this.fireDataStorageChanged(DataStorageEvent.DATA_CHANGED);
	}
	
	/**
	 * @return genes
	 */
	public GeneList getGenes() {
		return this.genes;
	}
	
	/**
	 * @return snps
	 */
	public SNPList getGlobalSNPList() {
		return getSNPList("Global");
	}
	
	/**
	 * @return haplotypes
	 */
	public HaplotypesList getHaplotypes() {
		return this.haplotypes;
	}
	
	/**
	 * @return persons
	 */
	public SubjectList getSubjects() {
		return this.persons;
	}
	
	public MetaInformationManager getMetaInformationManager() {
		return this.metaInformationManager;
	}
	
	/**
	 * @param listener
	 */
	public void addDataStorageListener(DataStorageListener listener) {
		eventfirer.addListener(listener);
	}
	
	/**
	 * @param listener
	 */
	public void removeDataStorageListener(DataStorageListener listener) {
		eventfirer.removeListener(listener);
	}
	
	public Set<DataStorageListener> getDataStorageListeners() {
		return eventfirer.getListeners();
	}
	
	/**
	 * @param change
	 */
	public void fireDataStorageChanged(int change) {
		synchronized(this) {
			eventfirer.fireEvent(new DataStorageEvent(this, change));
		}
	}
	
	/**
	 * @return the associated mayday data set
	 */
	public DataSet getDataSet() {
		return this.maydayDataSet;
	}
	
	/**
	 * @param ds
	 */
	public void setDataSet(DataSet ds) {
		this.maydayDataSet = ds;
	}
	
	public void addLDStructure(LDStructure ld) {
		this.ldStructures.add(ld);
		fireDataStorageChanged(DataStorageEvent.DATA_CHANGED);
	}
	
	public LDStructure getLDStructure(int index) {
		if(index >= 0 && index < ldStructures.size())
			return this.ldStructures.get(index);
		return null;
	}

	public ArrayList<LDStructure> getLDStructures() {
		return this.ldStructures;
	}
	
	public void serialize(BufferedWriter bw) throws IOException {
		bw.append("$$DATASTORAGE\n");
		
		bw.append(attribute.getName());
		bw.append("\t");
		bw.append(attribute.getInformation());
		bw.append("\n");
		bw.append(maydayDataSet.getName());
		bw.append("\n");
		
		if(genes != null) {
			bw.append("$GENES\n");
			genes.serialize(bw);
		}
		
		bw.append("$SNPLISTS\n");
		for(SNPList sl : snpLists) {
			sl.serialize(bw);
		}
		
		if(persons != null) {
			bw.append("$PERSONS\n");
			persons.serialize(bw);
		}
		
		bw.append("$HAPLOTYPES\n");
		haplotypes.serialize(bw);
		
		this.metaInformationManager.serialize(bw);
	}

	public Collection<SNPList> getSNPLists() {
		return this.snpLists;
	}

	public ProjectHandler getProjectHandler() {
		return this.projectHandler;
	}
	
	public String toString() {
		return this.getAttribute().getName();
	}
}
