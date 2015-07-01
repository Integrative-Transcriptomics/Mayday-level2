package mayday.Reveal.filter;

import mayday.Reveal.data.SNVList;

public interface SNPListSelectionFilter {

	public boolean pass(SNVList sl);
}
