package mayday.GWAS.data.attributes;

public class Attribute {
	
	private String name;
	private String information;
	
	public Attribute() {
		this("Default", "null");
	}
	
	public Attribute(String name) {
		this(name, null);
	}
	
	public Attribute(String name, String information) {
		if(name == null)
			this.name = "Default";
		else
			this.name = name;
		if(information == null)
			this.information = "null";
		else
			this.information = information;
	}
	
	public String getInformation() {
		return this.information;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setInformation(String information) {
		this.information = information;
	}
}
