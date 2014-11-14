package mayday.GWAS.io.gff3;

import java.util.ArrayList;
import java.util.List;

public class GFFElement {

	private String source;
	private String feature;
	private String name;
	
	private String ID;
	int phase;
	
	private GFFElement parent;
	
	private List<GFFElement> children;
	
	private ChromosomalLocation chrLoc;
	
	public GFFElement(String source, String feature, String ID, String name, GFFElement parent, ChromosomalLocation chrLoc, int phase) {
		this.source = source;
		this.feature = feature;
		this.ID = ID;
		this.parent = parent;
		this.chrLoc = chrLoc;
		this.phase = phase;
		this.name = name;
		
		this.children = new ArrayList<GFFElement>();
	}
	
	public String getName() {
		return name;
	}
	
	public int getPhase() {
		return phase;
	}
	
	public void addChild(GFFElement e) {
		this.children.add(e);
	}
	
	public GFFElement getChild(int index) {
		return this.children.get(index);
	}
	
	public int numChildren() {
		return this.children.size();
	}
	
	public ChromosomalLocation getChromosomalLocation() {
		return this.chrLoc;
	}
	
	public boolean hasChildren() {
		return children.size() > 0;
	}
	
	public String getID() {
		return this.ID;
	}
	
	public GFFElement getParent() {
		return parent;
	}
	
	public boolean hasParent() {
		return parent != null;
	}
	
	public String getSource() {
		return source;
	}
	
	public String getFeature() {
		return feature;
	}
}
