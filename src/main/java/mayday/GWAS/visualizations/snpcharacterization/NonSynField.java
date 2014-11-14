package mayday.GWAS.visualizations.snpcharacterization;

public class NonSynField {

	protected boolean nonSynonymous = false;
	
	public NonSynField(boolean nonSyn) {
		this.nonSynonymous = nonSyn;
	}
	
	public String toString() {
		return Boolean.toString(nonSynonymous);
	}
}
