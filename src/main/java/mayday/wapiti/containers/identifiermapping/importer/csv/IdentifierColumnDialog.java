package mayday.wapiti.containers.identifiermapping.importer.csv;

import java.awt.event.ActionEvent;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.table.TableModel;

import mayday.core.gui.columnparse.ColumnTypeDialog;
import mayday.wapiti.containers.identifiermapping.importer.csv.IdentifierColumnTypes.CTYPE;

@SuppressWarnings("serial")
public class IdentifierColumnDialog extends ColumnTypeDialog<CTYPE> {

	public IdentifierColumnDialog(TableModel tableModel) {
		super(tableModel, 
				new IdentifierColumnTypes(),
				new IdentifierColumnTypeEstimator(tableModel),
				new IdentifierColumnTypeValidator()
				);
	}

	protected HashMap<CTYPE, Integer> asCol = new HashMap<CTYPE, Integer>();

	public HashMap<CTYPE, Integer> getColumns() {
		return asCol;
	}
	
	protected void makeMap() {
		asCol.clear();
		for (int i=0; i!=table.getColumnCount(); ++i) {
			asCol.put(getColumnType(i), i);
		}
	}
	
	protected AbstractAction getOKAction() {
		return new OKAction();
	}
	
	public class OKAction extends ColumnTypeDialog<CTYPE>.OKAction {
		public void actionPerformed(ActionEvent e) {
			makeMap();
			super.actionPerformed(e);			
		}
	}
	
}
