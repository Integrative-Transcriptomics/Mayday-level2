package it.genomering.render.paths;

import it.genomering.render.RingDimensions;
import it.genomering.structure.Block;
import it.genomering.structure.SuperGenome;

import java.awt.geom.Point2D;

@SuppressWarnings("serial")
public class SuperGenomePath extends GRPath {
	
	public SuperGenomePath(SuperGenome g, RingDimensions ringdim) {
		for (Block b : g.getBlocks()) {
			// draw inner segment
			addBlock(b, ringdim.getInnerRingRadiusInner(), ringdim.getInnerRingRadiusOuter(), ringdim);
			// draw outer segment
			addBlock(b, ringdim.getOuterRingRadiusInner(), ringdim.getOuterRingRadiusOuter(), ringdim);
		}
	}
	
	private void addBlock(Block b, double radius_inner, double radius_outer, RingDimensions ringdim) {
		double a1 = ringdim.getStartDegree(b);
		double a2 = ringdim.getEndDegree(b);
		
		Point2D p = getCurrentPoint();
		if(p == null)
			moveTo(0,0);
		
		addSegment(radius_inner, a1, a2, false);
		lineTo(polarToX(radius_outer, a2), polarToY(radius_outer,a2));
		addSegment(radius_outer, a2, a1, true);
//		lineTo(polarToX(radius_inner, a1), polarToY(radius_inner,a1));
		closePath();
	}
}
