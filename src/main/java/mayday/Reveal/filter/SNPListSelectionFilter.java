package mayday.Reveal.filter;

import mayday.Reveal.data.SNPList;

public interface SNPListSelectionFilter {

	public boolean pass(SNPList sl);
}
