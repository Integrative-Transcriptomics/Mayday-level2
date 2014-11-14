package mayday.motifsearch.gui.visual;

import javax.swing.table.TableRowSorter;

import mayday.motifsearch.model.SequenceComparator;

public class SequenceRowSorter
extends TableRowSorter<SequenceTableModel> {

    private SequenceComparator sequenceComparator = new SequenceComparator(
	    SequenceComparator.SORT_BY_NUMBER_OF_DIFFERENT_MOTIF_SITES);

    public SequenceRowSorter(SequenceTableModel sequenceTableModel) {
	super(sequenceTableModel);
	this.setComparator(0, sequenceComparator);
    }

    public final void setStoredSortMechanism(byte sortMechanism) {
	this.sequenceComparator.setStoredSortMechanism(sortMechanism);
    }

}
