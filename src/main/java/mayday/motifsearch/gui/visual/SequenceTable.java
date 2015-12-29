package mayday.motifsearch.gui.visual;

import java.awt.Color;

import javax.swing.JTable;
import java.awt.event.*;

import mayday.motifsearch.model.Sequence;

/**
 * Sequence table that visualises sequences with the help of a sequence cell
 * renderer and a sequence colorModel.
 * 
 * @author Frederik Weber
 * 
 */
public class SequenceTable
extends JTable {
    private static final long serialVersionUID = 1L;

    private MotifSelectPopupMenu motifSelectPopupMenu;

    public SequenceTable(SequenceCellRenderer sequenceCellRenderer,
	    SequenceTableModel sequenceTableModel) {

	super(sequenceTableModel);

	/* set initial table properties */
	this.setRowHeight(50);
	this.setShowGrid(false);
	this.setGridColor(Color.white);
	this.setShowHorizontalLines(true);
	this.setBackground(Color.WHITE);
	this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	this.getColumn("sequences").setCellRenderer(sequenceCellRenderer);
	this.getColumn("sequences").setPreferredWidth(300);
	this.getColumn("sequences").setMinWidth(70);
	this.getColumn("names").setPreferredWidth(65);
	this.getColumn("names").setMaxWidth(150);
	this.getColumn("pos").setPreferredWidth(30);
	this.getColumn("strand").setMaxWidth(40);
	this.getColumn("mean sign").setCellRenderer(new DoubleCellRender());
	this.getColumn("mean sign").setPreferredWidth(50);
	this.getColumn("length").setPreferredWidth(45);
	this.getColumn("different motifs").setPreferredWidth(50);
	this.getColumn("sites").setPreferredWidth(40);

    }

    public final MotifSelectPopupMenu getMotifSelectPopupMenu() {
	return this.motifSelectPopupMenu;
    }

    public final void setMotifSelectPopupMenu(MotifSelectPopupMenu selectPopupMenu) {
	this.motifSelectPopupMenu = selectPopupMenu;
    }

    @Override
    public String getToolTipText(MouseEvent e) {
	String tip = null;
	java.awt.Point p = e.getPoint();
	int rowIndex = rowAtPoint(p);

	Sequence sequence = (Sequence)  this.getValueAt(rowIndex, this.convertColumnIndexToView(0));

	tip = sequence.toString();

	return tip;
    }
}
