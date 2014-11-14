package it.genomering.render.paths;

import it.genomering.render.RingDimensions;
import it.genomering.structure.CoveredBlock;
import it.genomering.structure.Genome;
import it.genomering.structure.GenomePosition;

import java.awt.Graphics2D;

@SuppressWarnings("serial")
public class ElementsPath extends GRPath {

	private Genome g;
	private RingDimensions ringdim;
	
	public ElementsPath(Genome g, RingDimensions ringdim) {
		this.g = g;
		this.ringdim = ringdim;
	}
	
	public void addElement(int startBase, int endBase, Boolean forward) {
		
		GenomePosition startPos = new GenomePosition(g, startBase, null);
		GenomePosition endPos = new GenomePosition(g, endBase, null);
		
		if (!startPos.isValid() || !endPos.isValid())
			return;
		
		int start_block = startPos.gBlockIndex();
		int end_block = endPos.gBlockIndex();
		
		for (int cur_block = start_block; cur_block<=end_block; ++cur_block) {
			CoveredBlock cb = g.getBlocks().get(cur_block);
			// determine strand
			boolean fwd; 
			if (forward==null)
				fwd = cb.isForward();
			else
				fwd = forward;
			// determine radius
			double segment_radius = fwd?ringdim.getRadiusForward(g.getIndex()):ringdim.getRadiusBackward(g.getIndex());
			// adapt base position to block limits
			int start_base = Math.max(startBase, cb.getStart());
			int end_base = Math.min(endBase, cb.getEnd());
			double alpha_start = GenomePosition.asAngular(g, ringdim, cb, start_base);
			double alpha_end = GenomePosition.asAngular(g, ringdim, cb, end_base);
			addSegment(segment_radius, alpha_start, alpha_end, false);
		}
	}
	
	@Override
	public void paint(Graphics2D g2d) {
		if (g.isVisible())
			super.paint(g2d);
	}
	
}
