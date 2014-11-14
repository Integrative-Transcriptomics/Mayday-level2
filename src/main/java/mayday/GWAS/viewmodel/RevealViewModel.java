package mayday.GWAS.viewmodel;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import mayday.GWAS.data.DataStorage;
import mayday.GWAS.data.Gene;
import mayday.GWAS.data.Subject;
import mayday.GWAS.data.SNP;
import mayday.GWAS.data.SNPList;
import mayday.core.DataSet;
import mayday.core.ProbeList;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.Visualizer;

/**
 * @author jaeger
 *
 */
public class RevealViewModel extends ViewModel {
	
	Set<SNP> snpSelection;
	Set<Subject> personSelection;
	SNPSorter snpSorter;
	private DataStorage dataStorage;
	
	private SNPList topPrioritySNPList;

	/**
	 * @param viz
	 * @param dataset
	 * @param initialSelection
	 * @param dataStorage
	 * @param initialSNPSelection
	 */
	public RevealViewModel(Visualizer viz, DataSet dataset,
			List<ProbeList> initialSelection, DataStorage dataStorage, List<SNP> initialSNPSelection, List<Subject> initialPersonSelection) {
		super(viz, dataset, initialSelection);
		
		this.dataStorage = dataStorage;
		this.snpSelection = new HashSet<SNP>();
		this.personSelection = new HashSet<Subject>();
		
		if(initialSNPSelection != null) {
			snpSelection.addAll(initialSNPSelection);
		}
		
		if(initialPersonSelection != null)
			personSelection.addAll(initialPersonSelection);
		
		snpSorter = new SNPSorter(dataStorage);
	}
	
	/**
	 * get SNPs used for this visualizations
	 * @return all snps used for that visualization
	 */
	public List<SNP> getTopPrioritySNPList() {
		return Collections.unmodifiableList(this.topPrioritySNPList);
	}
	
	public void setTopPrioritySNPList(SNPList snpList) {
		this.topPrioritySNPList = snpList;
	}
	
	/**
	 * @return set of selected snps
	 */
	public Set<SNP> getSelectedSNPs() {
		return Collections.unmodifiableSet(snpSelection);
	}
	
	public Set<Subject> getSelectedPersons() {
		return Collections.unmodifiableSet(personSelection);
	}
	
	/**
	 * @param snp
	 * @return true if snp is selected, else false
	 */
	public boolean isSelected(SNP snp) {
		return snpSelection.contains(snp);
	}
	
	public boolean isSelected(Subject person) {
		return personSelection.contains(person);
	}
	
	/**
	 * @param snp
	 */
	public void selectSNP(SNP snp) {
		if (snpSelection.add(snp)) {
			eventfirer.fireEvent(new RevealViewModelEvent(this, RevealViewModelEvent.SNP_SELECTION_CHANGED));
		}
	}
	
	public void selectPerson(Subject person) {
		if(personSelection.add(person)) {
			eventfirer.fireEvent(new RevealViewModelEvent(this, RevealViewModelEvent.PERSON_SELECTION_CHANGED));
		}
	}
	
	/**
	 * @param snp
	 */
	public void unselectSNP(SNP snp) {
		if(snpSelection.remove(snp)) {
			eventfirer.fireEvent(new RevealViewModelEvent(this, RevealViewModelEvent.SNP_SELECTION_CHANGED));
		}
	}
	
	public void unselectPerson(Subject person) {
		if(personSelection.remove(person))
			eventfirer.fireEvent(new RevealViewModelEvent(this, RevealViewModelEvent.PERSON_SELECTION_CHANGED));
	}
	
	/**
	 * @param snp
	 */
	public void toggleSNPSelected(SNP snp) {
		if(snpSelection.contains(snp)) {
			unselectSNP(snp);
		} else {
			selectSNP(snp);
		}
	}
	
	public void togglePersonSelected(Subject person) {
		if(personSelection.contains(person)) {
			unselectPerson(person);
		} else {
			selectPerson(person);
		}
	}
	
	/**
	 * @param snps
	 */
	public void toggleSNPsSelected(Set<SNP> snps) {
		for(SNP snp : snps) {
			if(snpSelection.contains(snp)) {
				snpSelection.remove(snp);
			} else {
				snpSelection.add(snp);
			}
		}
		eventfirer.fireEvent(new RevealViewModelEvent(this, RevealViewModelEvent.SNP_SELECTION_CHANGED));
	}
	
	public void togglePersonsSelected(Set<Subject> persons) {
		for(Subject person: persons) {
			if(personSelection.contains(person)) {
				personSelection.remove(person);
			} else {
				personSelection.add(person);
			}
		}
		eventfirer.fireEvent(new RevealViewModelEvent(this, RevealViewModelEvent.PERSON_SELECTION_CHANGED));
	}
	
	/**
	 * @param newSelection
	 */
	public void setSNPSelection(Collection<SNP> newSelection) {
		setSNPSelectionSilent(newSelection);
		eventfirer.fireEvent(new RevealViewModelEvent(this, RevealViewModelEvent.SNP_SELECTION_CHANGED));
	}
	
	public void setPersonSelection(Collection<Subject> newPersons) {
		boolean changed = setPersonSelectionSilent(newPersons);
		if(changed) {
			eventfirer.fireEvent(new RevealViewModelEvent(this, RevealViewModelEvent.PERSON_SELECTION_CHANGED));
		}
	}
	
	/**
	 * @param newSelection
	 */
	public void setSNPSelectionSilent(Collection<SNP> newSelection) {
		if(newSelection == null) {
			snpSelection.clear();
			return;
		}
		if (newSelection.size()==snpSelection.size()) {
			Set<SNP> tmp = new HashSet<SNP>(snpSelection);
			tmp.removeAll(newSelection);
			if (tmp.size()==0)
				return;  // identical, no change
		}
		snpSelection.clear();
		snpSelection.addAll(newSelection);
	}
	
	public boolean setPersonSelectionSilent(Collection<Subject> newSelection) {
		if(newSelection == null) {
			personSelection.clear();
			return true;
		}
		if(newSelection.size() == personSelection.size()) {
			Set<Subject> tmp = new HashSet<Subject>(personSelection);
			tmp.removeAll(newSelection);
			if(tmp.size() == 0)
				return false; //identical, no change
		}
		personSelection.clear();
		personSelection.addAll(newSelection);
		return true;
	}
	
	/**
	 * @param snp
	 */
	public void setSNPSelection(SNP snp) {
		LinkedList<SNP> snps = new LinkedList<SNP>();
		if(snp != null) {
			snps.add(snp);
		}
		setSNPSelection(snps);
	}
	
	public void setPersonSelection(Subject person) {
		LinkedList<Subject> persons = new LinkedList<Subject>();
		if(person != null) {
			persons.add(person);
		}
		setPersonSelection(persons);
	}
	
	/**
	 * @param snps
	 * @param sortOption
	 * @param g
	 * @return sorted list of snps
	 */
	public SNPList sortSNPs(SNPList snps, String sortOption, Gene g) {
		return snpSorter.getSortedSNPList(snps, sortOption, g);
	}
	
	/**
	 * 
	 * @return the associated datastorage object
	 */
	public DataStorage getDataStorage() {
		return this.dataStorage;
	}
}
