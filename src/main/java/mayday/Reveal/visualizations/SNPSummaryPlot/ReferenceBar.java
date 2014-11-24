package mayday.Reveal.visualizations.SNPSummaryPlot;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
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
public class ReferenceBar {

	protected char reference, snp;
	
	/**
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param ref
	 */
	public ReferenceBar(char ref) {
		this.reference = ref;
		this.snp = ref;
	}
	
	public ReferenceBar(char ref, char snp) {
		this.reference = ref;
		this.snp = snp;
	}
	
	public void draw(Graphics2D g2d, AffineTransform af, double x, double y, double w, double h, boolean withChange) {
		if(withChange) {
			this.drawWithChange(g2d, af, x, y, w, h);
		} else {
			this.draw(g2d, af, x, y, w, h);
		}
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
		g2d.setColor(ATCGColors.getColor(reference));
		Rectangle2D referenceBox = new Rectangle2D.Double(0, 0, w, h);
		g2d.translate(x, y);
		
		g2d.fill(referenceBox);
	    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
		g2d.setPaint(new GradientPaint(0, 0, Color.BLACK, (float)w/2.f, 0, Color.WHITE, true));
		g2d.fill(referenceBox);
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.f));
		
		g2d.setColor(Color.BLACK);
		g2d.draw(new Rectangle2D.Double(0, 2, w, h-3));
		
		g2d.setColor(Color.WHITE);
		g2d.setStroke(new BasicStroke(3.f));
		g2d.draw(new Line2D.Double(0,0,w,0));
		g2d.setStroke(new BasicStroke(1.f));
		
		g2d.setColor(Color.BLACK);
		Rectangle2D s = g2d.getFontMetrics().getStringBounds(reference+"", g2d);
		g2d.translate(w/2.- s.getCenterX(), h/2.-s.getCenterY());
		g2d.drawString(reference+"", 0, 0);
		
		g2d.setTransform(af);
	}
	
	public void drawWithChange(Graphics2D g2d, AffineTransform af, double x, double y, double w, double h) {
		g2d.translate(x, y);
		
		Composite comp = g2d.getComposite();
		
		double w2 = w/2.;
		
		Rectangle2D referenceBox = new Rectangle2D.Double(0, 0, w2, h);
		Rectangle2D snpBox = new Rectangle2D.Double(w2, 0, w, h);
		g2d.setColor(ATCGColors.getColor(reference));
		g2d.fill(referenceBox);
		g2d.setColor(ATCGColors.getColor(snp));
		g2d.fill(snpBox);
	    
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
		g2d.setPaint(new GradientPaint(0, 0, Color.BLACK, (float)w2, 0, Color.WHITE, true));
		g2d.fill(referenceBox);
		g2d.fill(snpBox);
		
		g2d.setComposite(comp);
		
		g2d.setColor(Color.BLACK);
		g2d.draw(new Rectangle2D.Double(0, 2, w, h-3));
		g2d.setColor(Color.WHITE);
		g2d.fill(new Rectangle2D.Double(0,-2,w,3));
		
		g2d.setColor(Color.BLACK);
		Rectangle2D s = g2d.getFontMetrics().getStringBounds(reference+"", g2d);
		g2d.translate(w2/2.- s.getCenterX(), h/2.-s.getCenterY());
		g2d.drawString(reference+"", 0, 0);
		g2d.translate(w2,0);
		g2d.drawString(snp+"", 0, 0);
		
		g2d.setTransform(af);
	}
}
