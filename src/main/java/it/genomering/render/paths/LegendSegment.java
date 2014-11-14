package it.genomering.render.paths;

import it.genomering.render.RingDimensions;
import it.genomering.structure.SuperGenome;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

@SuppressWarnings("serial")
public class LegendSegment extends GRPath {

	protected String text="";
	protected double textX, textY;
	
	public LegendSegment(SuperGenome sg, RingDimensions ringdim, double angle_span, double min_angle_per_unit) {
		this.setStyle(Color.black, new BasicStroke((float) (ringdim.getGenomeWidth()*.2)));
		
		// find good scaling
		double degperunit=ringdim.getDegreePerBase();
		
		if (degperunit==0)
			return;

		int unit=1;		
		while (degperunit<min_angle_per_unit) {
			degperunit*=10;
			unit*=10;
		}
		
		// how many units in angle_span? --> find perfect angle_span 
		int units_displayed = (int)Math.round(angle_span/degperunit);
		angle_span = units_displayed*degperunit;
		
		int scale = (int)(Math.log(unit)/Math.log(1000));
		String unit_str="";
		int rest=0;
		switch (scale) {
		case 0: unit_str=" bp"; rest=unit; break;
		case 1: unit_str=" Kb"; rest=unit/1000; break;
		case 2: unit_str=" Mb"; rest=unit/1000000; break;
		case 3: unit_str=" Gb"; rest=unit/1000000000; break;
		}
//		scale = (int)Math.round(Math.log(rest)/Math.log(10));
//		switch (scale) {
//		case 0: unit_str=" "+unit_str; break;
//		case 1: unit_str="0 "+unit_str; break;
//		case 2: unit_str="00 "+unit_str; break;
//		case 3: unit_str="00 "+unit_str; break;
//		}
		text = rest*units_displayed + unit_str;	
		
		double alpha_start = 270-(angle_span/2d);
		double alpha_end = 270+(angle_span/2d);
		double inner_radius = ringdim.getLegendRadius();
		double outer_radius = inner_radius+ringdim.getGenomeWidth();
		
		addSegment(outer_radius, alpha_start, alpha_end, false);
		
		for (double alpha_tick=alpha_start; alpha_tick<=alpha_end; alpha_tick+=degperunit) {
			moveTo(polarToX(outer_radius,alpha_tick), polarToY(outer_radius,alpha_tick));
			lineTo(polarToX(inner_radius,alpha_tick), polarToY(inner_radius,alpha_tick));
		}
		moveTo(polarToX(outer_radius,alpha_end), polarToY(outer_radius,alpha_end));
		lineTo(polarToX(inner_radius,alpha_end), polarToY(inner_radius,alpha_end));
		
		textX = polarToX(outer_radius, alpha_end);
		textY = polarToY(outer_radius, alpha_end);
	
	}
	
	public void paint(Graphics2D g) {
		super.paint(g);
		AffineTransform t = g.getTransform();
		Point2D p = new Point2D.Double(textX, textY);
		t.transform(p, p);
		g.setTransform(new AffineTransform());
		
//		int width = (int)g.getFontMetrics().getStringBounds(text, g).getWidth();
		int height= (int)g.getFontMetrics().getStringBounds(text, g).getHeight();
		
		g.drawString(text, (int)p.getX()+5, (int)p.getY()+height/2);
		g.setTransform(t);
	}
}
