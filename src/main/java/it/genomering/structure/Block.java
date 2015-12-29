/**
 * 
 */
package it.genomering.structure;

public class Block {
	
	private final SuperGenome superGenome;
	
	protected int length;
	protected int start=-1; // start position of the block in the SuperGenome's coordinate system
	protected int offset=-1; // offset of the block in the SuperGenome
	protected int index=-1; // the index of this block in SuperGenome.blocks
	protected String name;
	
	protected int sortingIndex = -1;

	
	public Block(SuperGenome superGenome, String name, int length) {
		this.superGenome = superGenome;
		this.length = length;
		this.name = name;
	}
	
	public Block(SuperGenome superGenome, String name, int start, int end) {
		this.superGenome = superGenome;
		this.start=start;
		this.length=end-start+1;
		this.name = name;
	}
	
	public void setOffset(int offset) {
		this.offset=offset;
		if (start==-1)
			start = offset;
	}
	
	public int getLength() {
		return length;
	}
	public int getStart() {
		return start;
	}
	public int getEnd() {
		return start+length-1;
	}
	public int getIndex() {
		return index;
	}	
	
	public int getOffset() {
		return offset;
	}
	
	public String toString() {
		return "["+index+"] ("+start+"-"+(start+length-1)+")";
	}
	
	public double getStartPercentage() {
		return (double)getOffset()/(double)this.superGenome.getNumberOfBases();
	}
	
	public double getEndPercentage() {
		return (double)(getOffset()+getLength())/(double)this.superGenome.getNumberOfBases();
	}
	
	public String getName() {
		return name;
	}
	
	public String toOutputString() {
		return getStart() + "-" + getEnd();
	}
}