package mayday.Reveal.visualizations.SNVSummaryPlot.tracks;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import mayday.Reveal.data.SNVList;
import mayday.Reveal.utilities.ATCGColors;

public class SNVReferenceTrack extends SNVSummaryTrackComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6805918499024555080L;

	public SNVReferenceTrack(SNVSummaryTrack track) {
		super(track);
	}

	@Override
	public void doPaint(Graphics2D g2) {
		
		int cellWidth = track.getSetting().getCellWidth();
		
		int startIndex = track.getSetting().getStartIndex();
		int stopIndex = track.getSetting().getStopIndex();
		
		SNVList snps = track.getSelectedSNPs();
		
		for(int i = startIndex; i < stopIndex; i++) {
			char reference = snps.get(i).getReferenceNucleotide();
			AffineTransform af = g2.getTransform();
			
			g2.setColor(ATCGColors.getColor(reference));
			Rectangle2D referenceBox = new Rectangle2D.Double(0, 0, cellWidth, getHeight());
			g2.translate(cellWidth * i, -1);
			
			g2.fill(referenceBox);
		    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
			g2.setPaint(new GradientPaint(0, 0, Color.BLACK, (float)cellWidth/2.f, 0, Color.WHITE, true));
			g2.fill(referenceBox);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.f));
			
			g2.setColor(Color.BLACK);
			g2.draw(new Rectangle2D.Double(0, 2, cellWidth, getHeight()-3));
			
			g2.setColor(Color.WHITE);
			g2.setStroke(new BasicStroke(3.f));
			g2.draw(new Line2D.Double(0,0,cellWidth,0));
			g2.setStroke(new BasicStroke(1.f));
			
			
			
			g2.setColor(Color.BLACK);
			Rectangle2D s = g2.getFontMetrics().getStringBounds(reference+"", g2);
			
			if(cellWidth > s.getWidth()) {
				g2.translate(cellWidth/2.- s.getCenterX(), getHeight()/2.-s.getCenterY());
				g2.drawString(reference+"", 0, 0);
			}
			
			g2.setTransform(af);
		}
	}
}
