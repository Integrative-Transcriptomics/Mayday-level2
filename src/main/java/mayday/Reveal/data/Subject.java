package mayday.Reveal.data;

/**
 * @author jaeger
 *
 */
public class Subject {
	
	//FIXME include the sex sometime in the future?!
	private String familyID;
	private String ID;
	private Boolean affection;
	private String paternalID;
	private String maternalID;
	
	private String name = null;
	
	private int index;

	/**
	 * @param familyID
	 * @param ID
	 * @param affection
	 * @param index 
	 * @param expressionValues
	 */
	public Subject(String familyID, String ID, Boolean affection) {
		this.familyID = familyID;
		this.ID = ID;
		this.affection = affection;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	/**
	 * @param i
	 */
	public void setIndex(int i) {
		this.index = i;
	}
	
	/**
	 * the index of a person should match its index in the haplotypes list
	 * @return the index of this person
	 */
	public int getIndex() {
		return this.index;
	}
	
	/**
	 * @param familyID
	 * @param individualID
	 * @param paternalID
	 * @param maternalID
	 * @param sex 
	 * @param affection
	 */
	public Subject(String familyID, String individualID, String paternalID, String maternalID, Boolean affection) {
		this.familyID = familyID;
		this.ID = individualID;
		this.paternalID = paternalID;
		this.maternalID = maternalID;
		this.affection = affection;
	}
	
	/**
	 * @return paternal ID
	 */
	public String getPaternalID() {
		return this.paternalID;
	}
	
	/**
	 * @return maternal ID
	 */
	public String getMaternalID() {
		return this.maternalID;
	}
	
	/**
	 * @return familyID
	 */
	public String getFamilyID() {
		return this.familyID;
	}
	
	/**
	 * @return ID
	 */
	public String getIndividualID() {
		return this.ID;
	}
	
	/**
	 * @return true if affected, else false
	 */
	public Boolean affected() {
		return this.affection;
	}
	
	/**
	 * @return the identifier of this person
	 */
	public String getID() {
		return familyID+":"+ID;
	}
	
	public String toString() {
		return "Family ID: " + familyID + ", Individual ID: " + ID;
	}

	public String serialize() {
		StringBuffer b = new StringBuffer();
		b.append(familyID);
		b.append("\t");
		b.append(ID);
		b.append("\t");
		b.append(affection);
		b.append("\t");
		b.append(paternalID);
		b.append("\t");
		b.append(maternalID);
		b.append("\t");
		b.append(index);
		return b.toString();
	}
}
