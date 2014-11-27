package mayday.Reveal.visualizations.SNPSummaryPlot;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import mayday.Reveal.utilities.ATCGColors;

/**
 * @author guenter
 *
 */
public class FrequencyBar {

	protected double[] affected;
	protected double[] unaffected;
	
	private double maxSum;
	
	/**
	 * @param affected
	 * @param unaffected
	 */
	public FrequencyBar(double[] affected, double[] unaffected) {
		this.affected = affected;
		this.unaffected = unaffected;
		
		maxSum = getMaxSum();
		
		if(Double.isNaN(maxSum))
			maxSum = 1;
	}
	
	private double getMaxSum() {
		double sum = 0;
		for(int i = 0; i < affected.length; i++)
			sum += Math.max(affected[i], unaffected[i]);
		return sum;
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
		double maxHeight = 0;
		Color[] colorPairs = ATCGColors.getGenotypeColors();
		double hm = 1. / maxSum;
		double factor = (h-2) * hm;
		float w2 = (float)w/2.f;
		
		for(int i = 0; i < affected.length; i++) {
			Rectangle2D a = new Rectangle2D.Double(0, 0, w2, Double.isNaN(affected[i]) ? 0 : affected[i] * factor);
			Rectangle2D b = new Rectangle2D.Double(0, 0, w2, Double.isNaN(unaffected[i]) ? 0: unaffected[i] * factor);
			
			colorPairs = ATCGColors.getColorPairs(i);
			
			double difference = a.getHeight() - b.getHeight();
			
			//draw affected
			g2d.translate(x, maxHeight + y);
			AffineTransform af2 = g2d.getTransform();
			if(difference < 0) 
				g2d.translate(0, -difference);
			g2d.setPaint(new GradientPaint(0, 0, colorPairs[0], 3, 3, colorPairs[1], true));
			g2d.fill(a);
			
		    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
			g2d.setPaint(new GradientPaint(0, 0, Color.BLACK, w2, 0, Color.WHITE, false));
			g2d.fill(a);
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.f));
			
			g2d.setColor(Color.GRAY);
			g2d.draw(a);
			
			g2d.setTransform(af2);
			
			//draw unaffected
			g2d.translate(w/2., 0);
			if(difference > 0) 
				g2d.translate(0, difference);
			g2d.setPaint(new GradientPaint(0, 0, colorPairs[0], 3, 3, colorPairs[1], true));
			g2d.fill(b);
			
		    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
			g2d.setPaint(new GradientPaint((float)0, 0, Color.WHITE, w2, 0, Color.BLACK, false));
			g2d.fill(b);
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.f));
			
			g2d.setColor(Color.GRAY);
			g2d.draw(b);
			
			g2d.setTransform(af);
			
			maxHeight += Math.max(a.getHeight(), b.getHeight());
			
			g2d.translate(x, y);
			g2d.setColor(Color.BLACK);
			g2d.draw(new Rectangle2D.Double(0,0,w,h-2));
			
			g2d.setTransform(af);
		}
		
		g2d.setTransform(af);
	}
}
