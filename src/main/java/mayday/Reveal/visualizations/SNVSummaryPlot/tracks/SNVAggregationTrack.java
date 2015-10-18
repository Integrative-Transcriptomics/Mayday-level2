package mayday.Reveal.visualizations.SNVSummaryPlot.tracks;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

import mayday.Reveal.data.SNVList;
import mayday.Reveal.visualizations.SNVSummaryPlot.SummaryPlotFunctions;

public class SNVAggregationTrack extends SNVSummaryTrackComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -558862223395100682L;
	
	private double[][] affected;
	private double[][] unaffected;
	
	private double[] aggHeightA;
	private double[] aggHeightB;
	
	double[] totalHeight, hAf, hBf;

	public SNVAggregationTrack(SNVSummaryTrack track) {
		super(track);
		
		SNVList snpList = track.getSelectedSNPs();
		
		affected = new double[snpList.size()][];
		unaffected = new double[snpList.size()][];
		
		aggHeightA = new double[snpList.size()];
		aggHeightB = new double[snpList.size()];
		
		totalHeight = new double[snpList.size()];
		hAf = new double[snpList.size()];
		hBf = new double[snpList.size()];
		
		for(int i = 0; i < snpList.size(); i++) {
						
			affected[i] = SummaryPlotFunctions.getFrequencyForPairs(snpList.get(i).getIndex(), true, track.getDataStorage());
			unaffected[i] = SummaryPlotFunctions.getFrequencyForPairs(snpList.get(i).getIndex(), false, track.getDataStorage());
			
			char ref = snpList.get(i).getReferenceNucleotide();
			int reference = this.getCharIndex(ref);
			
			if(reference != -1) {
				this.aggHeightA[i] = getAggregatedHeight(affected[i], reference);
				this.aggHeightB[i] = getAggregatedHeight(unaffected[i], reference);
				
				this.totalHeight[i] = aggHeightA[i] + aggHeightB[i];
				this.hAf[i] = aggHeightA[i] / totalHeight[i];
				this.hBf[i] = aggHeightB[i] / totalHeight[i];
			}
		}
	}

	@Override
	public void doPaint(Graphics2D g2) {
		int cellWidth = track.getSetting().getCellWidth();
		
		int startIndex = track.getSetting().getStartIndex();
		int stopIndex = track.getSetting().getStopIndex();
		
		for(int i = startIndex; i < stopIndex; i++) {
			int x = cellWidth * i;
			
			if(track.getSetting().isAggregationStacked()) {
				this.paintStacked(g2, x, i);
			} else {
				this.paintAligned(g2, x, i);
			}
		}
	}
	
	private void paintAligned(Graphics2D g2, int x, int index) {
		int cellWidth = track.getSetting().getCellWidth();
		double w2 = cellWidth/2.;
		
		AffineTransform af = g2.getTransform();
		
		g2.translate(x, 0);
		
		SNVList snpList = track.getSelectedSNPs();
		char ref = snpList.get(index).getReferenceNucleotide();
		int reference = this.getCharIndex(ref);
		
		Color colorA = getAggregatedColor(affected[index], reference);
		Color colorB = getAggregatedColor(unaffected[index], reference);
		
		//affected
		double hagA = aggHeightA[index] * getHeight();
		Rectangle2D a = new Rectangle2D.Double(0,getHeight()-hagA,w2,hagA);
		g2.setPaint(new GradientPaint(0, 0, colorA.darker(), (float)w2, 0, colorA.brighter(), false));
		g2.fill(a);
		
		g2.setColor(Color.GRAY);
		g2.draw(new Line2D.Double(0,getHeight()-hagA,cellWidth/2.,getHeight()-hagA));
		
		//unaffected
		double hagB = aggHeightB[index] * getHeight();
		Rectangle2D b = new Rectangle2D.Double(0,getHeight()-hagB,w2,hagB);
		g2.translate(w2, 0);
		g2.setPaint(new GradientPaint(0, 0, colorB.brighter(), (float)w2, 0, colorB.darker(), false));
		g2.fill(b);
		
		g2.setColor(Color.GRAY);
		g2.draw(new Line2D.Double(0,getHeight()-hagB,cellWidth/2.,getHeight()-hagB));
		
		g2.setColor(Color.GRAY);
		g2.draw(new Line2D.Double(0, getHeight()-Math.max(hagA, hagB), 0, getHeight()));
		
		g2.setTransform(af);
		
		g2.setColor(Color.BLACK);
		g2.translate(x, 0);
		g2.draw(new Rectangle2D.Double(0, 0, cellWidth, getHeight()-2));
		
		g2.setTransform(af);
	}
	
	private void paintStacked(Graphics2D g2, int x, int index) {
		int cellWidth = track.getSetting().getCellWidth();
		AffineTransform af = g2.getTransform();
		
		SNVList snpList = track.getSelectedSNPs();
		char ref = snpList.get(index).getReferenceNucleotide();
		int reference = this.getCharIndex(ref);
		
		Color colorA = getAggregatedColor(affected[index], reference);
		Color colorB = getAggregatedColor(unaffected[index], reference);
		
		float w2 = (float)cellWidth/2.f;
		float h2 = (float)getHeight()/2.f;
		g2.translate(x, 0);
		
		double totalHeight = aggHeightA[index] + aggHeightB[index];
		double hA = aggHeightA[index] / totalHeight * getHeight();
		double hB = aggHeightB[index] / totalHeight * getHeight();
		double fA = hA * 0.1;
		double fB = hB * 0.1;
		
		//affected
		Path2D p1 = new Path2D.Double();
		p1.moveTo(0, fB);
		p1.lineTo(w2, 0);
		p1.lineTo(cellWidth, fB);
		p1.lineTo(cellWidth, hB - 2);
		p1.lineTo(0, hB - 2);
		p1.closePath();
		
		g2.setColor(colorB);
		g2.setPaint(new GradientPaint(0, 0, g2.getColor().darker(), w2, 0, g2.getColor(), true));
		g2.translate(0,getHeight() - hB);
		g2.fill(p1);
		g2.setColor(Color.GRAY);
		g2.draw(p1);
		
		//unaffected
		g2.setTransform(af);
		g2.translate(x, 0);
		
		Path2D p2 = new Path2D.Double();
		p2.moveTo(0, 0);
		p2.lineTo(cellWidth, 0);
		p2.lineTo(cellWidth, hA - fA);
		p2.lineTo(w2, hA - 2);
		p2.lineTo(0, hA - fA);
		p2.closePath();
		
		g2.setColor(colorA);
		g2.setPaint(new GradientPaint(0, 0, g2.getColor().darker(), w2, 0, g2.getColor(), true));
		g2.fill(p2);
		g2.setColor(Color.GRAY);
		g2.draw(p2);
		
		g2.setTransform(af);
		g2.translate(x, 0);
		g2.setColor(Color.BLUE.darker());
		g2.draw(new Line2D.Double(0, h2, cellWidth, h2));
		
		g2.setTransform(af);
	}
	
	protected double getAggregatedHeight(double[] values, int reference) {
		double[] aggregated = this.aggregate(values, reference);
		int index = this.getMaxIndex(aggregated);
		if(index > -1) {
			return aggregated[index];
		} else {
			return 0;
		}
	}
	
	protected Color getAggregatedColor(double[] values, int reference) {
		double[] aggregated = this.aggregate(values, reference);
		
		int index = this.getMaxIndex(aggregated);
		int alpha = 230;
		
		Color c;
		
		switch(index) {
		case 0: c = track.getSetting().getAggRefColor();break;
		case 1: c = track.getSetting().getAggHetColor();break;
		case 2: c = track.getSetting().getAggHomColor();break;
		default: c = track.getSetting().getAggRefColor();
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
		return maxIndex;
	}
	
	/*
	 * double array of length 3 : ref / het / hom
	 */
	private double[] aggregate(double[] values, int reference) {
		int nochange = 0; //nochange = AA
		if(reference == 1) nochange = 4; //nochange = TT
		if(reference == 2) nochange = 7; //nochange = CC
		if(reference == 3) nochange = 9; //nochange = GG
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
