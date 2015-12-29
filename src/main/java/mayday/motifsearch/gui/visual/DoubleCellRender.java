package mayday.motifsearch.gui.visual;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;

public class DoubleCellRender extends DefaultTableCellRenderer implements TableCellRenderer{
    private static final long serialVersionUID = 1L;

    public DoubleCellRender() {

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable,
     *      java.lang.Object, boolean, boolean, int, int)
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
	    boolean isSelected, boolean hasFocus, int row, int column) {

	return super.getTableCellRendererComponent(table, 
		(((Double)value) >= Double.MAX_VALUE-1?"NA":String.valueOf(value)), isSelected, hasFocus, row, column);

    }






}
