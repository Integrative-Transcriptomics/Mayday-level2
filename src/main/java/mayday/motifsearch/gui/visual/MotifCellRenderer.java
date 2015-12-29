package mayday.motifsearch.gui.visual;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import mayday.motifsearch.model.Motif;


/**
 * this class renders the colorModel for every cell of the motif table
 * containing a motif
 * 
 * @author Frederik Weber
 * 
 */

public class MotifCellRenderer
implements TableCellRenderer {

    private MotifView view;

    public MotifCellRenderer(MotifView view) {
	this.view = view;
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
	    boolean isSelected, boolean hasFocus, int row, int column) {
	view.setMotif((Motif) table.getValueAt(row, column));
	return view;
    }

}
