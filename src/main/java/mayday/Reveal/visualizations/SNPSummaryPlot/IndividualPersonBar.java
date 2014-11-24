package mayday.Reveal.visualizations.SNPSummaryPlot;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import mayday.Reveal.utilities.ATCGColors;

/**
 * @author jaeger
 *
 */
public class IndividualPersonBar {
	
	protected char allel1, allel2;
	
	/**
	 * @param allel1
	 * @param allel2
	 * @param affected
	 */
	public IndividualPersonBar(char allel1, char allel2) {
		this.allel1 = allel1;
		this.allel2 = allel2;
	}
	
	/**
	 * @param g2d
	 * @param af
	 * @param x 
	 * @param y 
	 * @param w 
	 * @param h 
	 */
	public void draw(Graphics2D g2d, AffineTransform af, double x, double y, double w, double h) {
		g2d.translate(x, y);
		
		g2d.setColor(ATCGColors.getColor(allel1));
		Rectangle2D a = new Rectangle2D.Double(0, 0, w, h);

		g2d.setPaint(new GradientPaint(0, 0, ATCGColors.getColor(allel1), 3, 3, ATCGColors.getColor(allel2), true));
		g2d.fill(a);
	    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
		g2d.setPaint(new GradientPaint(0, 0, Color.BLACK, (float)w/2.f, 0, Color.WHITE, true));
		g2d.fill(a);
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.f));
		
		g2d.setColor(ATCGColors.getColor(allel2));
		g2d.translate(0, h/2.+1);
		
//		Rectangle2D b = new Rectangle2D.Double(0,0,w,h/2.);
//		
//		g2d.fill(b);
//	    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
//		g2d.setPaint(new GradientPaint(0, 0, Color.BLACK, (float)w/2.f, 0, Color.WHITE, true));
//		g2d.fill(b);
//		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.f));
		
//		g2d.setColor(Color.GRAY);
//		g2d.draw(new Line2D.Double(0,0,0,h));
//		g2d.setColor(affected ? Color.RED : Color.GREEN);
//		g2d.setStroke(new BasicStroke(3.f));
//		g2d.draw(new Line2D.Double(0,0,w,0));
//		g2d.setStroke(new BasicStroke(1.f));
		
		g2d.translate(0, -h/2.);
		g2d.setStroke(new BasicStroke(3.f));
		g2d.setColor(Color.WHITE);
		g2d.draw(new Line2D.Double(-w/2.,0,w,0));
		g2d.setStroke(new BasicStroke(1.f));
		
		g2d.setTransform(af);
		g2d.translate(x, y);
		g2d.setColor(Color.BLACK);
		g2d.draw(new Rectangle2D.Double(0,3,w,h));
		
		
		String allels = allel1+""+allel2;
//		String a2 = allel2+"";
//		
		Rectangle2D allelsr = g2d.getFontMetrics().getStringBounds(allels, g2d);
//		Rectangle2D a2r = g2d.getFontMetrics().getStringBounds(a2, g2d);
//		
		g2d.translate(w/2.-allelsr.getCenterX(), h/2.-allelsr.getCenterY());
		g2d.drawString(allels, 0, 0);
//		g2d.translate(a1r.getCenterX()-a2r.getCenterX(), h/2.+a1r.getCenterY()-a2r.getCenterY());
//		g2d.drawString(a2, 0, 0);
		
		g2d.setTransform(af);
	}
}
