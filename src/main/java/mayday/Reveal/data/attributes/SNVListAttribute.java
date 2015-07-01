package mayday.Reveal.data.attributes;

import mayday.Reveal.data.SNVList;

public class SNVListAttribute extends Attribute {

	private SNVList snpList;
	
	public SNVListAttribute(SNVList snpList) {
		super();
		this.snpList = snpList;
	}
	
	public SNVListAttribute(SNVList snpList, String name, String information) {
		super(name, information);
		this.snpList = snpList;
	}
	
	public int getNumberOfSNPs() {
		return this.snpList.size();
	}
}
