package it.genomering.structure;

import it.genomering.render.RingDimensions;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import mayday.core.EventFirer;

public class Genome {

	private ArrayList<CoveredBlock> blocks;
	protected final SuperGenome superGenome;
	protected final boolean circular;
	
	protected String name;
	protected Color color;
	protected boolean visible = true;
	protected int total_length; 

	protected EventFirer<GenomeEvent, GenomeListener> firer = new EventFirer<GenomeEvent, GenomeListener>() {
		@Override
		protected void dispatchEvent(GenomeEvent event, GenomeListener listener) {
			listener.genomeChanged(event);
		}
	}; 
	
	
	public Genome(SuperGenome superGenome, boolean circular, String name) {
		if (circular)
			throw new RuntimeException("Path rendering for circular genomes is not yet implemented.");
		this.superGenome = superGenome;
		blocks = new ArrayList<CoveredBlock>();
		//		blocks_sorted = new ArrayList<CoveredBlock>();
		this.circular = circular;
		this.name = name;
	}

	public String toString() {
		return "Genome \""+name+"\" "+blocks.toString();
	}
	
	public String getName() {
		return name;
	}

	public boolean isVisible() {
		return visible;
	}
	
	public void setVisible(boolean visible) {
		if (this.visible!=visible) {
			this.visible=visible;
			fireChange(GenomeEvent.VISIBILITY_CHANGED);
		}
	}
	
	public void setName(String name) {
		if (!this.name.equals(name)) {
			this.name = name;
			fireChange(GenomeEvent.NAME_CHANGED);
		}
			
	}
	
	public void addCoveredBlock(CoveredBlock b) {
		blocks.add(b);
		total_length+=b.getLength();
	}

	public int getIndex() {
		return superGenome.getIndex(this);
	}

	public List<CoveredBlock> getBlocks() {
		return Collections.unmodifiableList(blocks);
	}


	public boolean isCircular() {
		return circular;
	}

	public void setColor(Color c) {
		if (color!=c) {
			color=c;
			fireChange(GenomeEvent.COLOR_CHANGED);
		}
	}

	public Color getColor() {
		return color;
	}

	public int getNumberOfBases() {
		return total_length;
	}
	
	public int getLastBase() {
		return blocks.get(blocks.size()-1).getEnd();
	}

	public int getMaximalOuterSkip(RingDimensions ringdim) {
		int mos=0;
		for (int i=0; i!=blocks.size(); ++i) {
			CoveredBlock b = blocks.get(i);
			CoveredBlock nextb = blocks.get((i+1) % blocks.size());
			if (b.isForward() && nextb.isForward()) { // outer jump
				int level = nextb.getIndex()-b.getIndex();
				if (level<0)
					level += ringdim.getNumberOfBlocks(); // wrap around
				mos = Math.max(mos, level);
			}
		}
		return mos;
	}
	
	
	public class BlockComparator implements Comparator<Block> {

		HashMap<Block, Integer> reverseMap = new HashMap<Block, Integer>();
		public BlockComparator() {
			for (int i=0; i!=blocks.size(); ++i) {
				reverseMap.put(blocks.get(i).getBlock(),i);
			}
		}
		
		@Override
		public int compare(Block o1, Block o2) {
			Integer b1 = reverseMap.get(o1);
			Integer b2 = reverseMap.get(o2);
			if (b1==null)
				return -1;
			if (b2==null)
				return 1;
			return b1.compareTo(b2);
		}
	}

	public void resortSuperGenome() {
		List<Block> blocks = new ArrayList<Block>(superGenome.getBlocks());
		Collections.sort(blocks, new BlockComparator());
		superGenome.setBlocks(blocks);
	}

	public boolean containsBlock(Block b, Boolean fwd) {
		for (CoveredBlock cb : getBlocks())
			if (cb.getBlock()==b && cb.isForward()==fwd)
				return true;
		return false;
	}
	
	public void addListener(GenomeListener gl) {
		firer.addListener(gl);
	}
	
	public void removeListener(GenomeListener gl) {
		firer.removeListener(gl);
	}
	
	public void fireChange(int change) { // viewmodel events are fired through ConnectionManager into this function
		firer.fireEvent(new GenomeEvent(this, change));
	}
}