package mayday.Reveal.visualizations.SNVSummaryPlot.tracks;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import mayday.Reveal.data.SNVList;
import mayday.Reveal.utilities.ATCGColors;
import mayday.Reveal.visualizations.SNVSummaryPlot.SummaryPlotFunctions;

public class SNVDistributionTrack extends SNVSummaryTrackComponent {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 221180502654164046L;

	protected double[][] affected;
	protected double[][] unaffected;
	
	private double[] maxSum;
	
	public SNVDistributionTrack(SNVSummaryTrack track) {
		super(track);
		
		SNVList snpList = track.getSelectedSNPs();
		
		//distributions of affected and unaffected subjects
		affected = new double[snpList.size()][];
		unaffected = new double[snpList.size()][];
		
		//sum of the max sum of distribution values for affected/unaffected
		//used to determine total height of each bar in the distribution plot
		maxSum = new double[snpList.size()];
		
		for(int i = 0; i < snpList.size(); i++) {
			affected[i] = SummaryPlotFunctions.getFrequencyForPairs(snpList.get(i).getIndex(), true, track.getDataStorage());
			unaffected[i] = SummaryPlotFunctions.getFrequencyForPairs(snpList.get(i).getIndex(), false, track.getDataStorage());
			maxSum[i] = getMaxSum(affected[i], unaffected[i]);
			
			if(Double.isNaN(maxSum[i])) {
				maxSum[i] = 1;
			}
		}
	}

	@Override
	public void doPaint(Graphics2D g2) {
		int cellWidth = track.getSetting().getCellWidth();
		
		int startIndex = track.getSetting().getStartIndex();
		int stopIndex = track.getSetting().getStopIndex();
		
		for(int i = startIndex; i < stopIndex; i++) {
			AffineTransform af = g2.getTransform();
			
			double maxHeight = 0;
			Color[] colorPairs = ATCGColors.getGenotypeColors();
			double hm = 1. / maxSum[i];
			double factor = (getHeight()-2) * hm;
			float w2 = (float)cellWidth/2.f;
			
			for(int j = 0; j < affected[i].length; j++) {
				Rectangle2D a = new Rectangle2D.Double(0, 0, w2, Double.isNaN(affected[i][j]) ? 0 : affected[i][j] * factor);
				Rectangle2D b = new Rectangle2D.Double(0, 0, w2, Double.isNaN(unaffected[i][j]) ? 0: unaffected[i][j] * factor);
				
				colorPairs = ATCGColors.getColorPairs(j);
				
				double difference = a.getHeight() - b.getHeight();
				
				//draw affected
				g2.translate(cellWidth * i, maxHeight);
				AffineTransform af2 = g2.getTransform();
				if(difference < 0) 
					g2.translate(0, -difference);
				g2.setPaint(new GradientPaint(0, 0, colorPairs[0], 3, 3, colorPairs[1], true));
				g2.fill(a);
				
			    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
				g2.setPaint(new GradientPaint(0, 0, Color.BLACK, w2, 0, Color.WHITE, false));
				g2.fill(a);
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.f));
				
				g2.setColor(Color.GRAY);
				g2.draw(a);
				
				g2.setTransform(af2);
				
				//draw unaffected
				g2.translate(cellWidth/2., 0);
				if(difference > 0) 
					g2.translate(0, difference);
				g2.setPaint(new GradientPaint(0, 0, colorPairs[0], 3, 3, colorPairs[1], true));
				g2.fill(b);
				
			    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
				g2.setPaint(new GradientPaint((float)0, 0, Color.WHITE, w2, 0, Color.BLACK, false));
				g2.fill(b);
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.f));
				
				g2.setColor(Color.GRAY);
				g2.draw(b);
				
				g2.setTransform(af);
				
				maxHeight += Math.max(a.getHeight(), b.getHeight());
				
				g2.translate(cellWidth * i, 0);
				g2.setColor(Color.BLACK);
				g2.draw(new Rectangle2D.Double(0,0,cellWidth,getHeight()-2));
				
				g2.setTransform(af);
			}
			
			g2.setTransform(af);
		}
	}
	
	private double getMaxSum(double[] affected, double[] unaffected) {
		double sum = 0;
		for(int i = 0; i < affected.length; i++)
			sum += Math.max(affected[i], unaffected[i]);
		return sum;
	}
	
	
}
