package mayday.Reveal.visualizations.SNPSummaryPlot;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

/**
 * @author jaeger
 *
 */
public class HistBar {
	
	protected String label;
	
	/**
	 * @param label 
	 */
	public HistBar(String label) {		
		this.label = label;
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
		//snp label
		int space = 2;
		Rectangle2D s = g2d.getFontMetrics().getStringBounds(label, g2d);
		g2d.setColor(Color.BLACK);
		g2d.translate(x + w/2 + s.getCenterY(), h-s.getWidth()-space);
		g2d.rotate(Math.toRadians(90));
		g2d.drawString(label, 0, 0);
		g2d.setTransform(af);
	}
}
