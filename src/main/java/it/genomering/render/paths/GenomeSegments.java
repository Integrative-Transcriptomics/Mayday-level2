package it.genomering.render.paths;

import it.genomering.render.RingDimensions;
import it.genomering.structure.CoveredBlock;
import it.genomering.structure.Genome;

import java.util.List;

@SuppressWarnings("serial")
public class GenomeSegments extends GRPath {

	public GenomeSegments(Genome g, RingDimensions ringdim) {
		
		int gidx = g.getIndex();
		List<CoveredBlock> blocks = g.getBlocks(); // blocks sorted in order of genome
		
		for (int i=0; i!=blocks.size(); ++i) {
			CoveredBlock b = blocks.get(i);
			// add block
			addBlock(b, ringdim, gidx);
		
		}
				
	}
	
	private void addBlock(CoveredBlock b, RingDimensions ringdim, int gidx) {
		double segment_radius = b.isForward()?ringdim.getRadiusForward(gidx):ringdim.getRadiusBackward(gidx);
		double alpha_start = ringdim.getStartDegree(b);
		double alpha_end = ringdim.getEndDegree(b);
		addSegment(segment_radius, alpha_start, alpha_end, false);
	}	
	
	
}
