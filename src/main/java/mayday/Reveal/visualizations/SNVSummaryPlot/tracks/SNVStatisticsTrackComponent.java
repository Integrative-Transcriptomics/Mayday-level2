package mayday.Reveal.visualizations.SNVSummaryPlot.tracks;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.List;

import mayday.Reveal.data.SNVList;
import mayday.Reveal.data.meta.MetaInformation;
import mayday.Reveal.data.meta.StatisticalTestResult;

public class SNVStatisticsTrackComponent extends SNVSummaryTrackComponent {

	public SNVStatisticsTrackComponent(SNVSummaryTrack track) {
		super(track);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 7333708088298384615L;

	@Override
	public void doPaint(Graphics2D g2) {
		int cellWidth = track.getSetting().getCellWidth();
		
		int startIndex = track.getSetting().getStartIndex();
		int stopIndex = track.getSetting().getStopIndex();
		
		SNVList snps = track.getSelectedSNPs();
		List<MetaInformation> mis = track.getDataStorage().getMetaInformationManager().get(StatisticalTestResult.MYTYPE);
		StatisticalTestResult res = (StatisticalTestResult)mis.get(track.getSetting().getStatTestIndex());
		
		double multiplier = 1;
		
		if(track.getSetting().showStatTestValues()) {
			int digits = track.getSetting().getPValueDigits();
			multiplier = Math.pow(10, digits);
		}

		double pMin = Math.round(-Math.log10(res.getMin())*100.)/100.;
		int space = 2;
		
		for(int i = startIndex; i < stopIndex; i++) {
			AffineTransform af = g2.getTransform();
		
			double pVal = Math.round(-Math.log10(res.getPValue(snps.get(i)))*100.)/100.;
			
			int h = (int)Math.floor((pVal / pMin) * getHeight());
			
			g2.setColor(Color.WHITE);
			Rectangle2D pValueBox = new Rectangle2D.Double(0, 0, cellWidth, h);
			g2.translate(cellWidth * i, getHeight() - h);
			
			g2.fill(pValueBox);
		    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
			g2.setPaint(new GradientPaint(0, 0, Color.BLACK, (float)cellWidth/2.f, 0, Color.WHITE, true));
			g2.fill(pValueBox);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.f));
			
			g2.setColor(Color.BLACK);
			g2.draw(new Rectangle2D.Double(0, 0, cellWidth, h));
			
			g2.setTransform(af);
			
			if(track.getSetting().showStatTestValues()) {
				
				String pString = Math.round(res.getPValue(snps.get(i)) * multiplier)/multiplier + "";
				
				g2.setColor(Color.BLACK);
				Rectangle2D s = g2.getFontMetrics().getStringBounds(pString, g2);
				
				if(cellWidth > s.getHeight()) {
					g2.translate(cellWidth * i + cellWidth/2 + s.getCenterY(), getHeight()-s.getWidth()-space);
					g2.rotate(Math.toRadians(90));
					g2.drawString(pString, 0, 0);
				}
			}
			
			g2.setTransform(af);
		}
	}
}
