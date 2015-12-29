package it.genomering.render.paths;

import it.genomering.render.RingDimensions;
import it.genomering.structure.CoveredBlock;
import it.genomering.structure.Genome;

@SuppressWarnings("serial")
public class GenomeFlags extends GRPath {

	public GenomeFlags(Genome g, RingDimensions ringdim) {
		
		double flag_outer = ringdim.getInnerRingRadiusInner() * .9;
		double flag_inner = flag_outer - 3 * ringdim.getGenomeWidth();

		double shift = ringdim.getNumberOfGenomes()/2. - g.getIndex();
		
		double circle_radius = ringdim.getGenomeWidth();
		
		CoveredBlock genome_first_block =  g.getBlocks().get(0);
		CoveredBlock genome_last_block =  g.getBlocks().get(g.getBlocks().size()-1);
		
		double start_flag_angle = genome_first_block.isDrawnClockwise() 
									? ringdim.getStartDegree(genome_first_block) 
									: ringdim.getEndDegree(genome_first_block);
									
		double end_flag_angle = genome_last_block.isDrawnClockwise() 
					  			? ringdim.getEndDegree(genome_last_block) 
								: ringdim.getStartDegree(genome_last_block);
		
	    double extent = 2;
		double start_flag_triangle_extent = genome_first_block.isDrawnClockwise() ? -extent : extent;
		double end_flag_triangle_extent = genome_last_block.isDrawnClockwise() ? extent : -extent;
					  			
		// START OF GENOME flag
		// move to start side of the flag pole
		moveTo(	polarToX(flag_outer - circle_radius, start_flag_angle+shift),
				polarToY(flag_outer - circle_radius, start_flag_angle+shift));
		// line to top of flag pole
		lineTo( polarToX(flag_outer, start_flag_angle+shift),
				polarToY(flag_outer, start_flag_angle+shift));
		// down and out for triangle
		lineTo( polarToX(flag_outer - circle_radius/2., start_flag_angle+start_flag_triangle_extent+shift), 
				polarToY(flag_outer - circle_radius/2., start_flag_angle+start_flag_triangle_extent+shift));
		//close triangle
		closePath();
		//foot of the flag
		lineTo(polarToX(flag_inner, start_flag_angle),
				polarToY(flag_inner, start_flag_angle));
			
		// END OF GENOME flag
		// down to center of triangle tip
		moveTo( polarToX(flag_inner + circle_radius/2.0, end_flag_angle+shift), 
				polarToY(flag_inner + circle_radius/2.0, end_flag_angle+shift));
		// down and out to lower triangle edge
		lineTo( polarToX(flag_inner+circle_radius, end_flag_angle+shift+end_flag_triangle_extent), 
				polarToY(flag_inner+circle_radius, end_flag_angle+shift+end_flag_triangle_extent) );
		// up for triangle back side
		lineTo( polarToX(flag_inner, end_flag_angle+shift+end_flag_triangle_extent), 
				polarToY(flag_inner, end_flag_angle+shift+end_flag_triangle_extent) );
		//close triangle
		closePath();
		
		//triangle end line
		lineTo( polarToX(flag_inner, end_flag_angle+shift),
				polarToY(flag_inner, end_flag_angle+shift));
		
		lineTo( polarToX(flag_inner + circle_radius, end_flag_angle+shift),
				polarToY(flag_inner + circle_radius, end_flag_angle+shift));
		
		//foot of the flag
		lineTo( polarToX(flag_outer, end_flag_angle), 
				polarToY(flag_outer, end_flag_angle));
	}
}
