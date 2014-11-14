package mayday.motifsearch.gui.visual;

import java.util.ArrayList;
import java.util.Collections;

import javax.swing.table.AbstractTableModel;

import mayday.motifsearch.gui.listeners.*;
import mayday.motifsearch.model.Motif;
import mayday.motifsearch.model.Sequence;
import mayday.motifsearch.model.SequenceComparator;
import mayday.motifsearch.model.Sequences;
import mayday.motifsearch.model.Sites;
import mayday.motifsearch.tool.Rounder;

/**
 * a model for a sequence table
 * 
 * @author Frederik Weber
 * 
 */
public class SequenceTableModel
	extends AbstractTableModel
	implements 
	AndOrSelectionListener,
	MotifChangeListener, 
	ColorModelListener {

    private static final long serialVersionUID = 1L;
    private Sequences sequences;
    private Sequences activatedSequences;
    private ArrayList<Motif> activatedMotifs;
    private SequenceTable sequenceTable;
    private ArrayList<ActivatedSequencesListener> listeners = new ArrayList<ActivatedSequencesListener>();
    
    private boolean isOrModusNotAndModus = true;

    public SequenceTableModel(Sequences sequences, ArrayList<Motif> motifs) {
	this.setSequences(sequences);
	this.activatedSequences = new Sequences();
	this.setActivatedSequences(motifs);
	this.activatedMotifs = motifs;
    }

    /**
     * sets the sequences
     * 
     * @param sequences
     *                the sequences to be set
     */
    public void setSequences(Sequences sequences) {
	if (this.sequences != null) {
	    // this.allTheSequences.removePromotorsListener(this);
	}
	this.sequences = sequences;
	if (this.sequences != null) {
	    // this.allTheSequences.addPromotorsListener(this);
	}

	if (this.activatedMotifs != null) {
	    this.setActivatedSequences(this.activatedMotifs);
	    this.fireTableDataChanged();
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getColumnCount() 
     */
    public int getColumnCount() {
	return 8;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.AbstractTableModel#getColumnClass(int) 
     *
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
	switch (columnIndex) {
	    case 0:
		return SequenceView.class;
	    case 1:
		return String.class;
	    case 2: 
		return Integer.class; 
	    case 3: 
		return Integer.class; 
	    case 4: 
		return String.class;
	    case 5: 
		return Double.class;
	    case 6: 
		return Long.class;
	    case 7: 
		return Integer.class;
//		 * case 4: return Double.class;
//		 */
	    default:
		throw new RuntimeException();
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.AbstractTableModel#getColumnName(int) 
     *     
     */
    @Override
    public String getColumnName(int column) {
	switch (column) {
	    case 0:
		return "sequences";
	    case 1:
		return "names";
	    case 2:
		return "sites";
	    case 3:
		return "different motifs";
	    case 4:
		return "strand";
	    case 5:
		return "mean sign";
	    case 6:
		return "length";
	    case 7:
		return "pos";
	    default:
		throw new RuntimeException();
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     *      Weber
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
	Sequence s = this.activatedSequences.get(rowIndex);
	switch (columnIndex) {
	    case 0:
		return s;
	    case 1:
		return s.getName();
	    case 2:
		return s.getSites().size();
	    case 3:
		return s.getSites().getNumberOfDifferentMotifs();
	    case 4:
		return (s.isPlusStrand()?"+":"-");
	    case 5:
		return Rounder.round(
			s.getMeanSignificanceOfSites(), 3);
	    case 6:
		return s.getLength();
	    case 7:
		return (this.sequenceTable.convertRowIndexToView(rowIndex)+1);
	    default:
		throw new RuntimeException("unexpected");
	}
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
	return false; // no cell is editable
    }

    public int getRowCount() {
	if (this.activatedSequences == null)
	    return 0;
	return this.activatedSequences.size();
    }

    public Sequence getSequence(int index) {
	return this.activatedSequences.get(index);
    }

    /**
     * motifChanged reports changes of motifs in the table  
     * 
     */

    public void motifChanged(ArrayList<Motif> selectedMotifs) {
    	System.out.println("STM : Motif changed event!");
	this.activatedMotifs = selectedMotifs;
	    this.setActivatedSequences(selectedMotifs);
	    this.fireTableDataChanged();
    }

    /**
     * sets the sequences that are painted
     * 
     * @param selectedMotifs
     *                list of motifs
     */
    private void setActivatedSequences(ArrayList<Motif> selectedMotifs) {

	this.activatedSequences.clear();
	if (this.isOrModusNotAndModus) {

	    /* or modus*/
	    for (Sequence s : this.sequences) {
		Sequence sClone = s.cloneBasics();
		s.updateSiteListOR(selectedMotifs, sClone);
		this.activatedSequences.add(sClone);
	    }
	}
	else {

	    /* and modus */
	    for (Sequence s : this.sequences) {
		Sequence sClone = s.cloneBasics();
		if (s.updateSiteListAND(selectedMotifs, sClone)) {
		    this.activatedSequences.add(sClone);
		}
		else {
		    sClone.setSites(new Sites());
		    this.activatedSequences.add(sClone);
		}
	    }
	}
	this.fireActivatedSequencesChanged(this.activatedSequences);
    }

    /**
     * Returns the activated Sequences
     * 
     * @return the activate sequences of this model
     */
    public final Sequences getSequences() {
	return this.activatedSequences;

    }

    /**
     * Returns the activated Sequences with a sort Type given by
     * SequenceComparator
     * 
     * @param sortType
     *                sort type for the sequence comparator
     */
    public final Sequences getSequences(byte sortType) {
	Collections.sort(this.activatedSequences, new SequenceComparator(
		sortType));
	return this.activatedSequences; // .clone();

    }

    /**
     * AndSelected is called if the Button And/Or is pressed, and sets the
     * isOrModusNotAndModus Variable to true if Or is pressed and to false if And is
     * pressed.
     * 
     * @param isAndSelected
     *                boolean true => And | false => Or
     */

 public final void isOrModusNotAndModusChanged(boolean isOrModusNotAndModus) {
		this.isOrModusNotAndModus = isOrModusNotAndModus;
	    this.setActivatedSequences(this.activatedMotifs);
	    this.fireTableDataChanged();
 }
 
 public void colorModelChanged(boolean isWhiteColorModel){
	this.fireTableDataChanged();
 }


    public void addActivatedSequencesListener(ActivatedSequencesListener l) {
	this.listeners.add(l);
    }

    public void removeActivatedSequencesListener(ActivatedSequencesListener l) {
	this.listeners.remove(l);
    }

    protected void fireActivatedSequencesChanged(Sequences activatedSequences){
	for (ActivatedSequencesListener l : this.listeners) {
	    l.setActivatedSequences(this.activatedSequences);
	}
    }

    
    public Sequences getActivatedSequences() {
        return activatedSequences;
    }
    public Sequences getUnEmptySequences() {
	Sequences ss = new Sequences();
	for (Sequence s : this.activatedSequences){
	    if (!s.getSites().isEmpty()){
		ss.add(s);
	    }
	}
        return ss;
    }
    
    public void setSequenceTable(SequenceTable sequenceTable) {
        this.sequenceTable = sequenceTable;
    }



}
