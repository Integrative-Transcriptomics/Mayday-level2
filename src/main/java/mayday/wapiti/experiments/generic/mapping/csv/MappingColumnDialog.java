package mayday.wapiti.experiments.generic.mapping.csv;

import java.awt.event.ActionEvent;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.table.TableModel;

import mayday.core.gui.columnparse.ColumnTypeDialog;
import mayday.wapiti.experiments.generic.mapping.csv.MappingColumnTypes.CTYPE;

@SuppressWarnings("serial")
public class MappingColumnDialog extends ColumnTypeDialog<CTYPE> {
	

	public MappingColumnDialog(TableModel tableModel) {
		super(tableModel, 
				new MappingColumnTypes(),
				new MappingColumnTypeEstimator(tableModel),
				new MappingColumnTypeValidator()
				);
	}

	protected HashMap<CTYPE, Integer> asCol = new HashMap<CTYPE, Integer>();

	protected void init() {
		super.init();
	}
	
	public HashMap<CTYPE, Integer> getColumns() {
		return asCol;
	}
	
	protected void makeMap() {
		asCol.clear();
		for (int i=0; i!=table.getColumnCount()-1; ++i) {
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
