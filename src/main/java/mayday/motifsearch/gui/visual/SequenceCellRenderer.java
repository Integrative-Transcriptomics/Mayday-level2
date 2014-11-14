package mayday.motifsearch.gui.visual;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import mayday.motifsearch.model.Sequence;


/**
 * this class renders the colorModel for every cell of the sequence table containing a
 * sequence
 * 
 * @author Frederik Weber
 * 
 */
public class SequenceCellRenderer
implements TableCellRenderer{

    private SequenceView view;

    /**
     * constructor of a sequence cell renderer
     * 
     * @see javax.swing.table.TableCellRenderer
     * @param minSignificanceValue
     *                the minimal significance value of all motifs
     * @param sequenceView
     *                the sequence colorModel to render
     */
    public SequenceCellRenderer(SequenceView sequenceView) {
	this.view = sequenceView;

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable,
     *      java.lang.Object, boolean, boolean, int, int)
     */
    public Component getTableCellRendererComponent(JTable table, Object value,
	    boolean isSelected, boolean hasFocus, int row, int column) {
	this.view.setSequence((Sequence) value);

	/*
	 * get the longest sequence length from the SequenceTableModel
	 * of the table
	 */
	this.view.setlongestSequenceLength(((SequenceTableModel) table
		.getModel()).getSequences()
		.getLongestSequenceLength());
	return this.view;
    }

    public final SequenceView getView() {
	return this.view;
    }



}
