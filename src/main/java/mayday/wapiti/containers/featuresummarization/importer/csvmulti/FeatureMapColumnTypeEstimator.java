package mayday.wapiti.containers.featuresummarization.importer.csvmulti;

import javax.swing.table.TableModel;

import mayday.core.gui.columnparse.ColumnTypeEstimator;
import mayday.wapiti.containers.featuresummarization.importer.csvmulti.FeatureMapColumnTypes.CTYPE;


public class FeatureMapColumnTypeEstimator extends ColumnTypeEstimator<CTYPE> {

	public FeatureMapColumnTypeEstimator(TableModel tableModel) {
		super(tableModel);
	}
	
	protected boolean hasID = false;
	protected boolean hasMIO = false;
	
	@SuppressWarnings("unchecked")
	@Override
	protected CTYPE estimateColumn(int i) {

		if (isValidColumn(i, new NoMissingChecker(), null)) {
			if (!hasID) {
				hasID = true;
				return CTYPE.Feature;
			}			
		}
		
		if (isValidColumn(i, new NoMissingChecker(), null)) {
			if (!hasMIO) {
				hasMIO = true;
				return CTYPE.Subfeature;
			}
			return null;
		}		

		return null;
	}	

}
