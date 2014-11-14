package mayday.wapiti.containers.featuresummarization.importer.csvsingle;

import javax.swing.table.TableModel;

import mayday.core.gui.columnparse.ColumnTypeEstimator;
import mayday.wapiti.containers.featuresummarization.importer.csvsingle.FeatureMapColumnTypes.CTYPE;

public class FeatureMapColumnTypeEstimator extends ColumnTypeEstimator<CTYPE> {

	public FeatureMapColumnTypeEstimator(TableModel tableModel) {
		super(tableModel);
	}
	
	protected boolean hasID = false;
	protected boolean hasMIO = false;
	
	@SuppressWarnings("unchecked")
	@Override
	protected CTYPE estimateColumn(int i) {


		if (isValidColumn(i, new ListChecker(), null)) {
			if (!hasMIO) {
				hasMIO = true;
				return CTYPE.Subfeatures;
			}
			return null;
		}		
		//  
		if (isValidColumn(i, new NoMissingChecker(), null)) {
			if (!hasID) {
				hasID = true;
				return CTYPE.Feature;
			}			
		}
		return null;
	}
	
	
	protected class ListChecker implements ValueChecker<Object> {

		public boolean isValid(String value, Object memory) {
			return value.contains(",");
		}
		
	}

}
