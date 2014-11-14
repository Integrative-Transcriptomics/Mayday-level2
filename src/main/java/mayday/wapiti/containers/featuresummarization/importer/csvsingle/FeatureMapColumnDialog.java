package mayday.wapiti.containers.featuresummarization.importer.csvsingle;

import java.awt.event.ActionEvent;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.table.TableModel;

import mayday.core.gui.columnparse.ColumnTypeDialog;
import mayday.wapiti.containers.featuresummarization.importer.csvsingle.FeatureMapColumnTypes.CTYPE;

@SuppressWarnings("serial")
public class FeatureMapColumnDialog extends ColumnTypeDialog<CTYPE> {

	public FeatureMapColumnDialog(TableModel tableModel) {
		super(tableModel, 
				new FeatureMapColumnTypes(),
				new FeatureMapColumnTypeEstimator(tableModel),
				new FeatureMapColumnTypeValidator()
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
