package mayday.motifsearch.gui.visual;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import mayday.motifsearch.model.Motif;
import mayday.motifsearch.model.Site;
import mayday.core.gui.*;

public class MouseAdapterMotif
extends MouseAdapter {

    /* here the column that contains the motif (usually the same column with name "color")*/
    private final int COLUMN_SHOWING_SELECTION = 0;
//    private final int COLUMN_SHOWING_SITES = 1;
    private final int COLUMN_CONTAINING_MOTIF = 2;
//    private final int COLUMN_CONTAINING_MOTIF_NAME = 3;
//    private final int COLUMN_CONTAINING_MOTIF_SIGNIFICANCE = 4;
    private final int COLUMN_SHOWING_SEQUENCELOGO = 5;
//    private final int COLUMN_CONTAINING_MOTIF_LENGTH = 6;


    private MotifTable table;
    MaydayDialog mf;

    /**
     * constructor of MouseAdapterMotif
     * 
     * @param table
     *                the motif table this adapter is used for
     * 
     */
    public MouseAdapterMotif(MotifTable table) {
	super();
	this.table = table;
    }

    /**
     * @see java.awt.event.MouseAdapter#mouseClicked(MouseEvent)
     * 
     */
    @Override
    public void mouseClicked(MouseEvent e) {
	MotifSelectPopupMenu pm = this.table.getMotifSelectPopupMenu();
	if (pm != null) {
	    pm.setVisible(false);
	}
	/* get rows and colums */
	int row = table.rowAtPoint(new Point(e.getX(), e.getY()));
	int column = this.table.convertColumnIndexToModel(table.columnAtPoint(new Point(e.getX(), e.getY())));
	/*
	 * if the column is not the column to select the activeness of the motif
	 */
	if ((column != this.COLUMN_SHOWING_SELECTION) && (column != this.COLUMN_SHOWING_SEQUENCELOGO)) {
	    /*get the motif from the motif model from the right column*/
	    Motif mo = (Motif) this.table.getValueAt(row, this.table.convertColumnIndexToView(this.COLUMN_CONTAINING_MOTIF));
	    ArrayList<Site> bs = new ArrayList<Site>();

	    bs.add(new Site(mo, 0)); // add dummy Site

	    /* set a new specific PopupMenue and show it */
	    this.table.setMotifSelectPopupMenu(new MotifSelectPopupMenu(bs,
		    this.table.getSiteSelectionModel(),
		    MotifSelectPopupMenu.ENABLE_DISABLE_DECITION_ONLY));
	    this.table.getMotifSelectPopupMenu().show(this.table, e.getX() + 10,
		    e.getY());
	}else if(column == this.COLUMN_SHOWING_SEQUENCELOGO) {
	    /*get the motif from the motif model from the right column*/
	    Motif mo = (Motif) this.table.getValueAt(row, this.table.convertColumnIndexToView(this.COLUMN_CONTAINING_MOTIF));

	    if (this.mf != null){
		this.mf.dispose();
	    }

	    this.mf = new MaydayDialog(null,mo.toString()); 

	    SequenceLogoView clonedSequenceLogoView = ((SequenceLogoCellRenderer)table.getCellRenderer(row, this.table.convertColumnIndexToView(column))).getView().cloneForPreview();
	    clonedSequenceLogoView.setAreCoorinatesPainted(true);
	    this.mf.getContentPane().add(clonedSequenceLogoView);

	    this.mf.setSize(new Dimension(mo.getLength()*30,200));
	    this.mf.setLocation(e.getXOnScreen() - 10 - this.mf.getWidth(),
		    e.getYOnScreen());
	    this.mf.setVisible(true);

	}

    }
}
