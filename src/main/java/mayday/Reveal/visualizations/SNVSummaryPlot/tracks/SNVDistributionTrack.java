package mayday.Reveal.visualizations.SNVSummaryPlot.tracks;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;

import mayday.Reveal.data.Haplotypes;
import mayday.Reveal.data.HaplotypesList;
import mayday.Reveal.data.SNVList;
import mayday.Reveal.data.Subject;
import mayday.Reveal.data.SubjectList;
import mayday.Reveal.utilities.ATCGColors;

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
		
		affected = new double[snpList.size()][];
		unaffected = new double[snpList.size()][];
		
		maxSum = new double[snpList.size()];
		
		for(int i = 0; i < snpList.size(); i++) {
			affected[i] = getFrequencyForPairs(snpList.get(i).getIndex(), true);
			unaffected[i] = getFrequencyForPairs(snpList.get(i).getIndex(), false);
			maxSum[i] = getMaxSum(affected[i], unaffected[i]);
			if(Double.isNaN(maxSum[i]))
				maxSum[i] = 1;
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
	
	private double[] getFrequencyForPairs(Integer snpIndex, boolean affected) {
		int[] count = new int[10];
		Arrays.fill(count, 0);
		
		HaplotypesList haplotypesList = track.getDataStorage().getHaplotypes();
		SubjectList personList = track.getDataStorage().getSubjects();
		
		if(affected) {
			ArrayList<Subject> affectedPersons = personList.getAffectedSubjects();
			for(Subject p : affectedPersons) {
				Haplotypes h = haplotypesList.get(p.getIndex());
				char[] snpPairs = new char[]{h.getSNPA(snpIndex), h.getSNPB(snpIndex)};
				Arrays.sort(snpPairs);
				count[ATCGColors.getPairIndex(snpPairs[0], snpPairs[1])]++;
			}
			double[] result = new double[10];
			for(int j = 0; j < result.length; j++) {
				result[j] = count[j] / (double)affectedPersons.size();
			}
			return result;
		} else {
			ArrayList<Subject> unaffectedPersons = personList.getUnaffectedSubjects();
			for(Subject p : unaffectedPersons) {
				Haplotypes h = haplotypesList.get(p.getIndex());
				
				char[] snpPairs = new char[]{h.getSNPA(snpIndex), h.getSNPB(snpIndex)};
				Arrays.sort(snpPairs);
				count[ATCGColors.getPairIndex(snpPairs[0], snpPairs[1])]++;
			}
			double[] result = new double[10];
			for(int j = 0; j < result.length; j++) {
				result[j] = count[j] / (double)unaffectedPersons.size();
			}
			return result;
		}
	}
}
