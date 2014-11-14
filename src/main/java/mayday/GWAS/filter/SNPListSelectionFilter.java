package mayday.GWAS.filter;

import mayday.GWAS.data.SNPList;

public interface SNPListSelectionFilter {

	public boolean pass(SNPList sl);
}
