package mayday.Reveal.data.attributes;

import mayday.Reveal.data.SNPList;

public class SNPListAttribute extends Attribute {

	private SNPList snpList;
	
	public SNPListAttribute(SNPList snpList) {
		super();
		this.snpList = snpList;
	}
	
	public SNPListAttribute(SNPList snpList, String name, String information) {
		super(name, information);
		this.snpList = snpList;
	}
	
	public int getNumberOfSNPs() {
		return this.snpList.size();
	}
}
