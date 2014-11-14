package mayday.GWAS.visualizations.snpcharacterization;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class SequenceComparisonField extends JPanel {

	protected String original;
	protected String modifiedA;
	protected String modifiedB;
	
	public SequenceComparisonField(String original, String modifiedA, String modifiedB) {
		this.original = original;
		this.modifiedA = modifiedA;
		this.modifiedB = modifiedB;
	}
	
	public String toString() {
		if(original == null || modifiedA == null || modifiedB == null)
			return "";
		if(original.equals(modifiedA) && original.equals(modifiedB))
			return "";
		
		if(modifiedA.equals(modifiedB)) {
			return original + " -> " + modifiedA;
		} else {
			if(!modifiedA.equals(original)) {
				return original + " -> " + modifiedA;
			} else {
				return original + " -> " + modifiedB;
			}
		}
	}
}
