package mayday.motifsearch.gui.visual;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.event.MouseEvent;

import javax.swing.JTable;

import mayday.motifsearch.model.Motif;


/**
 * Table that visualizes motifs with the help of a
 * motif cell renderer and a motif colorModel. it holds
 * information of the visualization of the sequences in the sequence table
 * 
 * @author Frederik Weber
 * 
 */

public class MotifTable
extends JTable {

    private static final long serialVersionUID = 1L;

    private SiteSelectionModel siteSelectionModel;
    private MotifSelectPopupMenu motifSelectPopupMenu;

    /**
     * @param motifCellRenderer
     *                a motif cell renderer for this table
     * @param motifTableModel
     *                a model for this table
     */
    public MotifTable(MotifCellRenderer motifCellRenderer, SequenceLogoCellRenderer sequenceLogoCellRenderer,
	    MotifTableModel motifTableModel) {

	super(motifTableModel);
	super.setOpaque(true);
	/* initialize the table */
	this.setShowGrid(false);
	this.setGridColor(Color.WHITE);
	this.setShowHorizontalLines(true);
	this.setBackground(Color.WHITE);
	this.setRowHeight(50);
	this.getColumn("selected").setMaxWidth(60);
	this.getColumn("selected").setMinWidth(20);
	this.getColumn("color").setMaxWidth(60);
	this.getColumn("name").setMaxWidth(60);
	this.getColumn("#sites").setMinWidth(20);
	this.getColumn("#sites").setMaxWidth(60);
	this.getColumn("logo").setMinWidth(100);
	this.getColumn("length").setMinWidth(20);

	this.getColumn("color").setCellRenderer(motifCellRenderer);
	this.getColumn("logo").setCellRenderer(sequenceLogoCellRenderer);
	this.getTableHeader()
	.setSize(new Dimension(this.getColumnModel()
		.getTotalColumnWidth(), 50));
	//	this.getTableHeader().setPreferredSize(new Dimension(getTableHeader().getWidth(), 100));

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics) 
     */
    @Override
    protected void paintComponent(Graphics g) {
	super.paintComponent(g);
	this.setOpaque(true);
    }

    @Override
    public String getToolTipText(MouseEvent e) {
	String tip = null;
	java.awt.Point p = e.getPoint();
	int rowIndex = rowAtPoint(p);

	Motif motif = (Motif)   this.getValueAt(rowIndex, this.convertColumnIndexToView(2));

	tip = motif.toString();

	return tip;
    }

    /**
     * sets the site selection model
     * 
     */
    protected void setSiteSelectionModel(
	    SiteSelectionModel siteSelectionModel) {
	this.siteSelectionModel = siteSelectionModel;
    }

    public final SiteSelectionModel getSiteSelectionModel() {
	return siteSelectionModel;
    }

    public final MotifSelectPopupMenu getMotifSelectPopupMenu() {
	return motifSelectPopupMenu;
    }

    public final void setMotifSelectPopupMenu(MotifSelectPopupMenu selectPopupMenu) {
	motifSelectPopupMenu = selectPopupMenu;
    }

}
