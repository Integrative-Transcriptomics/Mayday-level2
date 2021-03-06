package mayday.motifsearch.gui.visual;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import mayday.motifsearch.model.Motif;


public class SequenceLogoCellRenderer implements TableCellRenderer {

    private SequenceLogoView view;

    public SequenceLogoCellRenderer(SequenceLogoView view) {
	this.view = view;
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
	    boolean isSelected, boolean hasFocus, int row, int column) {
	view.setMotif((Motif) table.getValueAt(row, column));
	return view;
    }

    public SequenceLogoView getView() {
	return view;
    }
}
