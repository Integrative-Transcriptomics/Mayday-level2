package mayday.Reveal.visualizations.SNPSummaryPlot;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

/**
 * @author jaeger
 *
 */
public class AggregationBar {

	protected int reference;
	
	protected double[] affected, unaffected;
	
	protected Color colorA, colorB;
	protected double aggHeightA, aggHeightB, totalHeight, hAf, hBf;
	
	/**
	 * @param affected
	 * @param unaffected
	 * @param ref
	 */
	public AggregationBar(double[] affected, double[] unaffected, char ref) {
		this.reference = this.getCharIndex(ref);
		
		if(reference != -1) {
			this.affected = affected;
			this.unaffected = unaffected;
			
			this.colorA = getAggregatedColor(affected);
			this.colorB = getAggregatedColor(unaffected);
			
			this.aggHeightA = getAggregatedHeight(affected);
			this.aggHeightB = getAggregatedHeight(unaffected);
			
			this.totalHeight = aggHeightA + aggHeightB;
			this.hAf = aggHeightA / totalHeight;
			this.hBf = aggHeightB / totalHeight;
		}
	}
	
	/**
	 * @param g2d
	 * @param af
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param stacked
	 */
	public void draw(Graphics2D g2d, AffineTransform af, double x, double y, double w, double h, boolean stacked) {
		if(reference == -1)
			return;
		if(stacked) {
			drawStacked(g2d, af, x, y, w, h);
		} else {
			drawAligned(g2d, af, x, y, w, h);
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
	public void drawAligned(Graphics2D g2d, AffineTransform af, double x, double y, double w, double h) {
		double w2 = w/2.;
		
		g2d.translate(x, y);
		
		//affected
		double hagA = aggHeightA*h;
		Rectangle2D a = new Rectangle2D.Double(0,h-hagA,w2,hagA);
		g2d.setPaint(new GradientPaint(0, 0, colorA.darker(), (float)w2, 0, colorA.brighter(), false));
		g2d.fill(a);
		
		g2d.setColor(Color.GRAY);
		g2d.draw(new Line2D.Double(0,h-hagA,w/2.,h-hagA));
		
		//unaffected
		double hagB = aggHeightB*h;
		Rectangle2D b = new Rectangle2D.Double(0,h-hagB,w2,hagB);
		g2d.translate(w2, 0);
		g2d.setPaint(new GradientPaint(0, 0, colorB.brighter(), (float)w2, 0, colorB.darker(), false));
		g2d.fill(b);
		
		g2d.setColor(Color.GRAY);
		g2d.draw(new Line2D.Double(0,h-hagB,w/2.,h-hagB));
		
		g2d.setColor(Color.GRAY);
		g2d.draw(new Line2D.Double(0, h-Math.max(hagA, hagB), 0, h));
		
		g2d.setTransform(af);
		
		g2d.setColor(Color.BLACK);
		g2d.translate(x, y);
		g2d.draw(new Rectangle2D.Double(0, 0, w, h-2));
		
		g2d.setTransform(af);
	}
	
	
	/**
	 * @param g2d
	 * @param af
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */
	public void drawStacked(Graphics2D g2d, AffineTransform af, double x, double y, double w, double h) {
		float w2 = (float)w/2.f;
		float h2 = (float)h/2.f;
		g2d.translate(x, y);
		
		double totalHeight = aggHeightA + aggHeightB;
		double hA = aggHeightA / totalHeight * h;
		double hB = aggHeightB / totalHeight * h;
		double fA = hA * 0.1;
		double fB = hB * 0.1;
		
		//affected
		Path2D p1 = new Path2D.Double();
		p1.moveTo(0, fB);
		p1.lineTo(w2, 0);
		p1.lineTo(w, fB);
		p1.lineTo(w, hB - 2);
		p1.lineTo(0, hB - 2);
		p1.closePath();
		
		g2d.setColor(colorB);
		g2d.setPaint(new GradientPaint(0, 0, g2d.getColor().darker(), w2, 0, g2d.getColor(), true));
		g2d.translate(0,h - hB);
		g2d.fill(p1);
		g2d.setColor(Color.GRAY);
		g2d.draw(p1);
		
		//unaffected
		g2d.setTransform(af);
		g2d.translate(x, y);
		
		Path2D p2 = new Path2D.Double();
		p2.moveTo(0, 0);
		p2.lineTo(w, 0);
		p2.lineTo(w, hA - fA);
		p2.lineTo(w2, hA - 2);
		p2.lineTo(0, hA - fA);
		p2.closePath();
		
		g2d.setColor(colorA);
		g2d.setPaint(new GradientPaint(0, 0, g2d.getColor().darker(), w2, 0, g2d.getColor(), true));
		g2d.fill(p2);
		g2d.setColor(Color.GRAY);
		g2d.draw(p2);
		
		g2d.setTransform(af);
		g2d.translate(x, y);
		g2d.setColor(Color.BLUE.darker());
		g2d.draw(new Line2D.Double(0, h2, w, h2));
		
		g2d.setTransform(af);
	}
	
	protected double getAggregatedHeight(double[] values) {
		double[] aggregated = this.aggregate(values, reference);
		int index = this.getMaxIndex(aggregated);
		if(index > -1) {
			return aggregated[index];
		} else {
			return 0;
		}
	}
	
	protected Color getAggregatedColor(double[] values) {
		double[] aggregated = this.aggregate(values, reference);
		
		int index = this.getMaxIndex(aggregated);
		int alpha = 230;
		
		Color c;
		
		switch(index) {
		case 0: c = Color.WHITE;break;//new Color(0, 60, 48);break;//new Color(77, 175, 74); break;
		case 1: c = Color.GRAY;break;//new Color(118, 42, 131);break;//new Color(245, 220, 70); break;//new Color(55, 126, 184); break;
		case 2: c = Color.DARK_GRAY.darker();break;//new Color(27, 158, 119);break;//new Color(228, 26, 28); break;
		default: c = Color.WHITE;
		}
		
		return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
	}
	
	private int getMaxIndex(double[] values) {
		double max = Double.MIN_VALUE;
		int maxIndex = -1;
		for(int i = 0; i < values.length; i++) {
			if(values[i] > max) {
				max = values[i];
				maxIndex = i;
			}
		}
//		if(maxIndex == -1)
//			System.out.println(Arrays.toString(values));
		return maxIndex;
	}
	
	private double[] aggregate(double[] values, int reference) {
		int nochange = 0;
		if(reference == 1) nochange = 4;
		if(reference == 2) nochange = 7;
		if(reference == 3) nochange = 9;
		double[] aggregated = new double[3];
		aggregated[0] = values[nochange];
		
		if(reference == 0) {
			aggregated[1] = values[1] + values[2] + values[3];
		}
		if(reference == 1) {
			aggregated[1] = values[1] + values[5] + values[6];
		}
		if(reference == 2) {
			aggregated[1] = values[2] + values[5] + values[8];
		}
		if(reference == 3) {
			aggregated[1] = values[3] + values[6] + values[8];
		}
		
		if(reference == 0) {
			aggregated[2] = values[4] + values[5] + values[6] + values[7] + values[8] + values[9];
		}
		if(reference == 1) {
			aggregated[2] = values[0] + values[2] + values[3] + values[7] + values[8] + values[9];
		}
		if(reference == 2) {
			aggregated[2] = values[0] + values[1] + values[3] + values[4] + values[6] + values[9];
		}
		if(reference == 3) {
			aggregated[2] = values[0] + values[1] + values[2] + values[4] + values[5] + values[7];
		}
		
		return aggregated;
	}
	
	private int getCharIndex(char c) {
		switch(c) {
		case 'A': return 0;
		case 'T': return 1;
		case 'C': return 2;
		case 'G': return 3;
		default: return -1;
		}
	}
}
