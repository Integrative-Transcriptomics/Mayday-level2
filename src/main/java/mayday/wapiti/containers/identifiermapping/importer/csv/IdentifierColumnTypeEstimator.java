package mayday.wapiti.containers.identifiermapping.importer.csv;

import javax.swing.table.TableModel;

import mayday.core.gui.columnparse.ColumnTypeEstimator;
import mayday.wapiti.containers.identifiermapping.importer.csv.IdentifierColumnTypes.CTYPE;

public class IdentifierColumnTypeEstimator extends ColumnTypeEstimator<CTYPE> {

	public IdentifierColumnTypeEstimator(TableModel tableModel) {
		super(tableModel);
	}
	
	protected boolean hasOld = false;
	protected boolean hasNew = false;
	
	@SuppressWarnings("unchecked")
	@Override
	protected CTYPE estimateColumn(int i) {
		if (isValidColumn(i, new NoMissingChecker(), null)) {
			if (!hasOld) {
				hasOld = true;
				return CTYPE.Identifier;
			} 
			if (!hasNew) {
				hasNew = true;
				return CTYPE.New_Identifier;
			}
		}
		return null;
	}

}
