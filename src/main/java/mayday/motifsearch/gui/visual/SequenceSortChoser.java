package mayday.motifsearch.gui.visual;

/**
 * This Panel handles the sorting of the sequences in the sequence table
 * 
 * @author Frederik Weber
 */

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import mayday.motifsearch.gui.*;
import mayday.motifsearch.model.SequenceComparator;

public class SequenceSortChoser
	extends JPanel
	implements ActionListener {

    private static final long serialVersionUID = 1L;

    private SequenceRowSorter sequenceRowSorter;
    private JComboBox selectSortBox;
    private JLabel sortby;
    private byte sortType = SequenceComparator.SORT_BY_NUMBER_OF_SITES; //standard sort type

    public SequenceSortChoser(SequenceRowSorter sequenceRowSorter) {

	super();

	/* init rowsorter */
	this.sequenceRowSorter = sequenceRowSorter;

	/* init visual parts */
	this.initElements();

	/* initialise gridbag layout / constraints */
	this.setLayout(new GridBagLayout());
	
	/* initialise gridbag layout and constraints */
	GridBagLayout gbl = new GridBagLayout();
	this.setLayout(gbl);
	this.selectSortBox
		.setToolTipText("chose a sort mechnism for the sequences");
	this.sortby.setToolTipText("chose a sort mechnism for the sequences");
	
	Layout.addComponentToGridBag(this, gbl, this.sortby, 0, 0, 1, 1, 0.05,
		1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
	Layout.addComponentToGridBag(this, gbl, this.selectSortBox, 1, 0, 1, 1,
		0.95, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
    }

    /**
     * if a action is performed on the select sort box the source is determined.
     * Then the appropriate action is performed
     * 
     * @param e
     * @see java.awt.event.ActionEvent
     * 
     */
    public void actionPerformed(ActionEvent e) {
	if (e.getSource() == this.selectSortBox) {
	    JComboBox tmpSortBox = (JComboBox) e.getSource();
	    String selectedMethod = (String) tmpSortBox.getSelectedItem();
	    this.setRowSorting(selectedMethod);
	    /* sort the sequences in the sequence table new */
	    this.sequenceRowSorter.sort();
	}
    }

    public final byte getSortType() {
	return sortType;
    }

    /**
     * initialization of components
     * 
     */
    private void initElements() {
	this.sortby = new JLabel("Sort by: ", SwingConstants.CENTER);
	/* Combobox for row sorting of sequence table*/
	Object[] obj = {"number of sites",
		"number of different motifs",
		"length of sequence",
		"overall significance"};
	this.selectSortBox = new JComboBox(obj);
	this.selectSortBox.addActionListener(this);
    }

    /**
     * sets the row sorting for the sequence row sorter
     * 
     */
    public void setRowSorting(String sortTypeUserSelected) {
	if (sortTypeUserSelected.equals("number of sites")) {
	    this.sortType = SequenceComparator.SORT_BY_NUMBER_OF_SITES;
	    this.sequenceRowSorter
		    .setStoredSortMechanism(SequenceComparator.SORT_BY_NUMBER_OF_SITES);
	}
	else if (sortTypeUserSelected
		.equals("number of different motifs")) {
	    this.sortType = SequenceComparator.SORT_BY_NUMBER_OF_DIFFERENT_MOTIF_SITES;
	    this.sequenceRowSorter
		    .setStoredSortMechanism(SequenceComparator.SORT_BY_NUMBER_OF_DIFFERENT_MOTIF_SITES);
	}
	else if (sortTypeUserSelected.equals("length of sequence")) {
	    this.sortType = SequenceComparator.SORT_BY_LENGTH_OF_SEQUENCE;
	    this.sequenceRowSorter
		    .setStoredSortMechanism(SequenceComparator.SORT_BY_LENGTH_OF_SEQUENCE);
	}
	else if (sortTypeUserSelected.equals("overall significance")) {
	    this.sortType = SequenceComparator.SORT_BY_OVERALL_SIGNIFICANCE_OF_SEQUENCE;
	    this.sequenceRowSorter
		    .setStoredSortMechanism(SequenceComparator.SORT_BY_OVERALL_SIGNIFICANCE_OF_SEQUENCE);
	}
	else {
	    System.err.println("wrong sorter for sequences selected");
	}
    }
}
