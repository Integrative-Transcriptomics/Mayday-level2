package mayday.wapiti.experiments.generic.reads.csv;

import javax.swing.table.TableModel;

import mayday.core.gui.columnparse.ColumnTypeEstimator;
import mayday.genetics.basic.Strand;
import mayday.wapiti.experiments.generic.reads.csv.MappingColumnTypes.CTYPE;

public class MappingColumnTypeEstimator extends ColumnTypeEstimator<CTYPE> {
	

	public MappingColumnTypeEstimator(TableModel tableModel) {
		super(tableModel);
	}
	
	protected boolean hasFrom = false;
	protected boolean hasTo = false;
	
	@SuppressWarnings("unchecked")
	@Override
	protected CTYPE estimateColumn(int i) {
		// try to find strand info
		if (isValidColumn(i, new StrandChecker(), null))
			return CTYPE.Strand;
		if (isValidColumn(i, new IntegerChecker(), null)) {
			if (!hasFrom) {
				hasFrom = true;
				return CTYPE.From;
			}
			if (!hasTo) {
				hasTo = true;
				return CTYPE.To;
			}
			return null; // ignore this column
		}
		// not numeric and not a strand 
		if (isValidColumn(i, new NoMissingChecker(), null)) {
			if (i==0) {
				return CTYPE.Chromosome; // more likely than species
			}		
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	protected class StrandChecker implements ValueChecker {

		protected Boolean isDPstrand = null;
		protected Boolean isFRstrand = null;
		
		public boolean isValid(String value, Object memory) {
			value = value.trim();
		
			if (value.length()==0)
				return false;
			
			char v = value.charAt(0);

			boolean isDP = v=='D' || v=='P';
			boolean isFR = v=='F' || v=='R';
			
			if (isDPstrand==null)
				isDPstrand = isDP;
			if (isFRstrand==null)
				isFRstrand = isFR;

			if (isDPstrand)
				return isDP;
			else if (isFRstrand) 
				return isFR;			
			else
				return Strand.validChar(v);
		}
		
	}


}
